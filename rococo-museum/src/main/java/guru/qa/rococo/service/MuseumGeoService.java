package guru.qa.rococo.service;

import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public List<GeoJson> getAllCountries() {
        return geoRepository.findAll()
                .stream()
                .map(GeoJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MuseumJson getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .map(MuseumJson::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Museum not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MuseumJson> getAllMuseums() {
        return museumRepository.findAll()
                .stream()
                .map(MuseumJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public MuseumJson addMuseum(MuseumJson museum) {
        CountryEntity countryEntity = countryRepository.findById(museum.geo().country().id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Can`t find country by id: " + museum.geo().country().id()));
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setTitle(museum.title());
        museumEntity.setDescription(museum.description());
        GeoEntity geoEntity = new GeoEntity();
        geoEntity.setCity(museum.geo().city());
        geoEntity.setCountry(countryEntity);
        geoEntity = geoRepository.save(geoEntity);
        museumEntity.setGeo(geoEntity);
        if (isPhotoString(museum.photo())) {
            museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        MuseumEntity savedMuseum = museumRepository.save(museumEntity);
        return MuseumJson.fromEntity(savedMuseum);
    }

    @Transactional
    public MuseumJson updateMuseum(MuseumJson museum) {
        MuseumEntity museumEntity = museumRepository.findById(museum.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Can`t find museum by id: " + museum.id()));
        CountryEntity countryEntity = countryRepository.findById(museum.geo().country().id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Can`t find country by id: " + museum.geo().country().id()));
        museumEntity.setTitle(museum.title());
        museumEntity.setDescription(museum.description());
        GeoEntity geoEntity = museumEntity.getGeo();
        geoEntity.setCity(museum.geo().city());
        geoEntity.setCountry(countryEntity);
        geoRepository.save(geoEntity);
        if (isPhotoString(museum.photo())) {
            museumEntity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        MuseumEntity savedMuseum = museumRepository.save(museumEntity);
        return MuseumJson.fromEntity(savedMuseum);
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}