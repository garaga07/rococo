package guru.qa.rococo.controller;

import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.MuseumGeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class MuseumGeoController {

    private final MuseumGeoService museumGeoService;

    @Autowired
    public MuseumGeoController(MuseumGeoService museumGeoService) {
        this.museumGeoService = museumGeoService;
    }

    @GetMapping("/country")
    public List<GeoJson> getAllCountries() {
        return museumGeoService.getAllCountries();
    }

    @GetMapping("/museum/{id}")
    public MuseumJson getMuseumById(@PathVariable UUID id) {
        return museumGeoService.getMuseumById(id);
    }

    @GetMapping("/museum")
    public List<MuseumJson> getAllMuseums() {
        return museumGeoService.getAllMuseums();
    }
}