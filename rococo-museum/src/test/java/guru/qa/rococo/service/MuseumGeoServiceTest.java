package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuseumGeoServiceTest {

    private static final String PHOTO_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";

    private MuseumGeoService museumGeoService;

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private GeoRepository geoRepository;

    @Mock
    private CountryRepository countryRepository;

    private UUID museumId;
    private UUID countryId;
    private UUID geoId;
    private MuseumEntity museumEntity;
    private CountryEntity countryEntity;
    private GeoEntity geoEntity;

    @BeforeEach
    void setUp() {
        museumGeoService = new MuseumGeoService(geoRepository, museumRepository, countryRepository);

        countryId = UUID.fromString("111e4567-e89b-12d3-a456-426614174000");
        geoId = UUID.fromString("333e4567-e89b-12d3-a456-426614174000");
        museumId = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");

        countryEntity = new CountryEntity();
        countryEntity.setId(countryId);
        countryEntity.setName("France");

        geoEntity = new GeoEntity();
        geoEntity.setId(geoId);
        geoEntity.setCity("Paris");
        geoEntity.setCountry(countryEntity);

        museumEntity = new MuseumEntity();
        museumEntity.setId(museumId);
        museumEntity.setTitle("Louvre Museum");
        museumEntity.setDescription("Famous museum in Paris");
        museumEntity.setGeo(geoEntity);
        museumEntity.setPhoto(PHOTO_PLACEHOLDER.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getMuseumByIdShouldReturnMuseum() {
        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));

        MuseumJson result = museumGeoService.getMuseumById(museumId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(museumId);
        assertThat(result.title()).isEqualTo("Louvre Museum");
        assertThat(result.description()).isEqualTo("Famous museum in Paris");
        assertThat(result.geo().city()).isEqualTo("Paris");
        assertThat(result.geo().country().name()).isEqualTo("France");
    }

    @Test
    void getMuseumByIdShouldThrowNotFoundException() {
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> museumGeoService.getMuseumById(museumId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("id: Музей не найден с id: " + museumId);
    }

    @Test
    void getAllMuseumsShouldReturnPagedMuseums() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MuseumEntity> museumPage = new PageImpl<>(List.of(museumEntity), pageable, 1);

        when(museumRepository.findAll(pageable)).thenReturn(museumPage);

        Page<MuseumJson> result = museumGeoService.getAllMuseums(pageable, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Louvre Museum");
    }

    @Test
    void getAllMuseumsShouldFilterByTitle() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MuseumEntity> museumPage = new PageImpl<>(List.of(museumEntity), pageable, 1);

        when(museumRepository.searchMuseums("Louvre", pageable)).thenReturn(museumPage);

        Page<MuseumJson> result = museumGeoService.getAllMuseums(pageable, "Louvre");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Louvre Museum");
    }

    @Test
    void addMuseumShouldSaveAndReturnMuseum() {
        MuseumJson newMuseum = new MuseumJson(
                null,
                "Musée d'Orsay",
                "Another famous museum in Paris",
                PHOTO_PLACEHOLDER,
                new GeoJson("Paris", new CountryJson(countryId, "France"))
        );

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
        when(geoRepository.findByCityAndCountryId("Paris", countryId)).thenReturn(geoEntity);
        when(museumRepository.save(any(MuseumEntity.class))).thenAnswer(invocation -> {
            MuseumEntity savedMuseum = invocation.getArgument(0);
            savedMuseum.setId(UUID.randomUUID());
            return savedMuseum;
        });

        MuseumJson result = museumGeoService.addMuseum(newMuseum);

        assertThat(result.title()).isEqualTo("Musée d'Orsay");
        assertThat(result.geo().city()).isEqualTo("Paris");
        assertThat(result.geo().country().name()).isEqualTo("France");
    }

    @Test
    void addMuseumShouldThrowNotFoundException() {
        MuseumJson newMuseum = new MuseumJson(
                null,
                "Unknown Museum",
                "Non-existing country",
                PHOTO_PLACEHOLDER,
                new GeoJson("Unknown City", new CountryJson(UUID.randomUUID(), "Unknown"))
        );

        when(countryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> museumGeoService.addMuseum(newMuseum))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("country.id: Страна не найдена");
    }

    @Test
    void updateMuseumShouldModifyAndReturnMuseum() {
        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
        when(geoRepository.findByCityAndCountryId("Paris", countryId)).thenReturn(geoEntity);
        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);

        MuseumJson updatedMuseum = new MuseumJson(
                museumId,
                "Updated Louvre",
                "Updated description",
                PHOTO_PLACEHOLDER,
                new GeoJson("Paris", new CountryJson(countryId, "France"))
        );

        MuseumJson result = museumGeoService.updateMuseum(updatedMuseum);

        assertThat(result.title()).isEqualTo("Updated Louvre");
        assertThat(result.description()).isEqualTo("Updated description");
    }

    @Test
    void updateMuseumShouldThrowBadRequestException() {
        MuseumJson invalidMuseum = new MuseumJson(null, "Invalid", "No ID", PHOTO_PLACEHOLDER, null);

        assertThatThrownBy(() -> museumGeoService.updateMuseum(invalidMuseum))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id: ID музея обязателен");
    }

    @Test
    void updateMuseumShouldThrowNotFoundException() {
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        MuseumJson updatedMuseum = new MuseumJson(
                museumId,
                "Invalid Museum",
                "Not found",
                PHOTO_PLACEHOLDER,
                null
        );

        assertThatThrownBy(() -> museumGeoService.updateMuseum(updatedMuseum))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("id: Музей не найден с id:");
    }

    @Test
    void getAllMuseumsShouldReturnEmptyPageIfNoMuseums() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MuseumEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(museumRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<MuseumJson> result = museumGeoService.getAllMuseums(pageable, null);

        assertThat(result.getContent()).isEmpty();
    }
}