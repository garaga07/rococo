package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.model.GeoJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoService {

    private final GeoRepository geoRepository;

    @Autowired
    public GeoService(GeoRepository geoRepository) {
        this.geoRepository = geoRepository;
    }

    @Transactional(readOnly = true)
    public List<GeoJson> getAllCountry() {
        return geoRepository.findAll()
                .stream()
                .map(GeoJson::fromEntity)
                .collect(Collectors.toList());
    }
}