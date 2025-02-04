package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    private static final String PHOTO_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";

    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    private UUID artistId;
    private ArtistEntity artistEntity;

    @BeforeEach
    void setUp() {
        artistService = new ArtistService(artistRepository);

        artistId = UUID.randomUUID();
        artistEntity = new ArtistEntity();
        artistEntity.setId(artistId);
        artistEntity.setName("Claude Monet");
        artistEntity.setBiography("French impressionist painter");
        artistEntity.setPhoto(PHOTO_PLACEHOLDER.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getArtistByIdShouldReturnArtist() {
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        ArtistJson result = artistService.getArtistById(artistId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(artistId);
        assertThat(result.name()).isEqualTo("Claude Monet");
        assertThat(result.biography()).isEqualTo("French impressionist painter");
        assertThat(result.photo()).isEqualTo(PHOTO_PLACEHOLDER);
    }

    @Test
    void getArtistByIdShouldThrowNotFoundException() {
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.getArtistById(artistId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Художник не найден с id: " + artistId);
    }

    @Test
    void getAllArtistsShouldReturnPagedArtists() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        List<ArtistEntity> artistEntities = List.of(artistEntity);
        Page<ArtistEntity> artistPage = new PageImpl<>(artistEntities, pageable, artistEntities.size());

        when(artistRepository.findAll(pageable)).thenReturn(artistPage);

        Page<ArtistJson> result = artistService.getAllArtists(pageable, null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Claude Monet");
    }

    @Test
    void getAllArtistsShouldSearchByName() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "Monet";
        List<ArtistEntity> artistEntities = List.of(artistEntity);
        Page<ArtistEntity> artistPage = new PageImpl<>(artistEntities, pageable, artistEntities.size());

        when(artistRepository.searchArtists(searchName, pageable)).thenReturn(artistPage);

        Page<ArtistJson> result = artistService.getAllArtists(pageable, searchName);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Claude Monet");
    }

    @Test
    void addArtistShouldSaveAndReturnArtist() {
        ArtistJson newArtist = new ArtistJson(null, "Vincent van Gogh", "Dutch post-impressionist painter", PHOTO_PLACEHOLDER);
        ArtistEntity savedEntity = new ArtistEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setName("Vincent van Gogh");
        savedEntity.setBiography("Dutch post-impressionist painter");
        savedEntity.setPhoto(PHOTO_PLACEHOLDER.getBytes(StandardCharsets.UTF_8));

        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(savedEntity);

        ArtistJson result = artistService.addArtist(newArtist);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Vincent van Gogh");
        assertThat(result.biography()).isEqualTo("Dutch post-impressionist painter");
        assertThat(result.photo()).isEqualTo(PHOTO_PLACEHOLDER);

        ArgumentCaptor<ArtistEntity> captor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(artistRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Vincent van Gogh");
    }

    @Test
    void updateArtistShouldModifyAndReturnArtist() {
        ArtistJson updatedArtist = new ArtistJson(artistId, "Claude Monet Updated", "Updated biography", PHOTO_PLACEHOLDER);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity);

        ArtistJson result = artistService.updateArtist(updatedArtist);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(artistId);
        assertThat(result.name()).isEqualTo("Claude Monet Updated");
        assertThat(result.biography()).isEqualTo("Updated biography");
        assertThat(result.photo()).isEqualTo(PHOTO_PLACEHOLDER);
    }

    @Test
    void updateArtistShouldThrowNotFoundException() {
        ArtistJson updatedArtist = new ArtistJson(artistId, "Claude Monet Updated", "Updated biography", PHOTO_PLACEHOLDER);
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.updateArtist(updatedArtist))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Художник не найден с id: " + artistId);
    }

    @Test
    void updateArtistShouldThrowBadRequestExceptionIfIdIsNull() {
        ArtistJson invalidArtist = new ArtistJson(null, "Michelangelo", "Italian sculptor and painter", PHOTO_PLACEHOLDER);

        assertThatThrownBy(() -> artistService.updateArtist(invalidArtist))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("ID художника обязателен для заполнения");
    }
}