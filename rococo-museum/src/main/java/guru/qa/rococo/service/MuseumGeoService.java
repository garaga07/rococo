package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuseumGeoService {

    private final GeoRepository geoRepository;
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public MuseumGeoService(GeoRepository geoRepository, MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.geoRepository = geoRepository;
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public MuseumJson getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .map(MuseumJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<MuseumJson> getAllMuseums(Pageable pageable, String title) {
        Page<MuseumEntity> museums;

        if (title != null && !title.isBlank()) {
            String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8).trim();
            museums = museumRepository.searchMuseums(decodedTitle, pageable);
        } else {
            museums = museumRepository.findAll(pageable);
        }

        List<MuseumJson> museumJsons = museums.stream()
                .map(MuseumJson::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(museumJsons, pageable, museums.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<CountryJson> getAllCountries(Pageable pageable) {
        Page<CountryEntity> countries = countryRepository.findAll(pageable);

        List<CountryJson> countryJsons = countries.getContent()
                .stream()
                .map(CountryJson::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(countryJsons, pageable, countries.getTotalElements());
    }

    @Transactional
    public MuseumJson addMuseum(MuseumJson museum) {
        UUID countryId = museum.geo().country().id();
        CountryEntity countryEntity = countryRepository.findById(countryId)
                .orElseThrow(() -> new NotFoundException("Country not found with id: " + countryId));

        GeoEntity geoEntity = getOrCreateGeo(museum, countryEntity);
        MuseumEntity museumEntity = createAndSaveMuseum(museum, geoEntity);

        return MuseumJson.fromEntity(museumEntity);
    }

    @Transactional
    public MuseumJson updateMuseum(MuseumJson museum) {
        UUID museumId = museum.id();
        UUID countryId = museum.geo().country().id();

        MuseumEntity museumEntity = museumRepository.findById(museumId)
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + museumId));
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