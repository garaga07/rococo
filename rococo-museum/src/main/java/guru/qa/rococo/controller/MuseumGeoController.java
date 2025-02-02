package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.MuseumGeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
    public Page<CountryJson> getAllCountries(Pageable pageable) {
        return museumGeoService.getAllCountries(pageable);
    }

    @GetMapping("/museum/{id}")
    public MuseumJson getMuseumById(@PathVariable UUID id) {
        return museumGeoService.getMuseumById(id);
    }

    @GetMapping("/museum")
    public Page<MuseumJson> getAllMuseums(Pageable pageable, @RequestParam(required = false) String title) {
        return museumGeoService.getAllMuseums(pageable, title);
    }

    @PostMapping("/museum")
    public MuseumJson addMuseum(@RequestBody MuseumJson museum) {
        return museumGeoService.addMuseum(museum);
    }

    @PatchMapping("/museum")
    public MuseumJson updateMuseum(@RequestBody MuseumJson museum) {
        return museumGeoService.updateMuseum(museum);
    }
}