package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuseumGeoService {

    private static final Logger LOG = LoggerFactory.getLogger(MuseumGeoService.class);

    private final GeoRepository geoRepository;
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumGeoService(GeoRepository geoRepository, MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.geoRepository = geoRepository;
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<GeoJson> getAllCountries() {
        return geoRepository.findAll()
                .stream()
                .map(GeoJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MuseumJson getMuseumById(@Nonnull String id) {
        UUID uuid = UUID.fromString(id);
        return museumRepository.findById(uuid)
                .map(MuseumJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MuseumJson> getAllMuseums() {
        return museumRepository.findAll()
                .stream()
                .map(MuseumJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        UUID countryId = UUID.fromString(museum.geo().country().id().toString());
        CountryEntity countryEntity = countryRepository.findById(countryId)
                .orElseThrow(() -> new NotFoundException("Country not found with id: " + countryId));

        GeoEntity geoEntity = getOrCreateGeo(museum, countryEntity);
        MuseumEntity museumEntity = createAndSaveMuseum(museum, geoEntity);

        return MuseumJson.fromEntity(museumEntity);
    }

    @Transactional
    public MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        UUID museumId = UUID.fromString(museum.id().toString());
        UUID countryId = UUID.fromString(museum.geo().country().id().toString());

        MuseumEntity museumEntity = museumRepository.findById(museumId)
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + museum.id()));
        CountryEntity countryEntity = countryRepository.findById(countryId)
                .orElseThrow(() -> new NotFoundException("Country not found with id: " + countryId));

        GeoEntity geoEntity = getOrCreateGeo(museum, countryEntity);
        museumEntity.setGeo(geoEntity);
        museumEntity.setTitle(museum.title().trim());
        museumEntity.setDescription(museum.description().trim());

        if (isPhotoString(museum.photo())) {
            museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }

        museumRepository.save(museumEntity);
        return MuseumJson.fromEntity(museumEntity);
    }

    private GeoEntity getOrCreateGeo(MuseumJson museum, CountryEntity countryEntity) {
        String city = museum.geo().city().trim();

        GeoEntity existingGeo = geoRepository.findByCityAndCountryId(city, countryEntity.getId());
        if (existingGeo != null) {
            return existingGeo;
        }

        GeoEntity geoEntity = new GeoEntity();
        geoEntity.setCity(city);
        geoEntity.setCountry(countryEntity);
        return geoRepository.save(geoEntity);
    }

    private MuseumEntity createAndSaveMuseum(MuseumJson museum, GeoEntity geoEntity) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setTitle(museum.title().trim());
        museumEntity.setDescription(museum.description().trim());
        museumEntity.setGeo(geoEntity);

        if (isPhotoString(museum.photo())) {
            museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        return museumRepository.save(museumEntity);
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image") && photo.contains(";base64,");
    }
}