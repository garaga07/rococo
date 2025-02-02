package guru.qa.rococo.controller;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.RestMuseumDataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Validated
public class MuseumGeoController {

    private final RestMuseumDataClient restMuseumDataClient;

    @Autowired
    public MuseumGeoController(RestMuseumDataClient restMuseumDataClient) {
        this.restMuseumDataClient = restMuseumDataClient;
    }

    @GetMapping("/museum/{id}")
    public MuseumJson getMuseumById(@PathVariable UUID id) {
        return restMuseumDataClient.getMuseumById(id);
    }

    @GetMapping("/museum")
    public Page<MuseumJson> getAllMuseums(
            Pageable pageable,
            @RequestParam(required = false) String title) {
        return restMuseumDataClient.getAllMuseums(pageable, title);
    }

    @GetMapping("/country")
    public Page<CountryJson> getAllCountries(Pageable pageable) {
        return restMuseumDataClient.getAllCountries(pageable);
    }

    @PostMapping("/museum")
    public MuseumJson addMuseum(@Valid @RequestBody MuseumJson museum) {
        return restMuseumDataClient.addMuseum(museum);
    }

    @PatchMapping("/museum")
    public MuseumJson updateMuseum(@Valid @RequestBody MuseumJson museum) {
        return restMuseumDataClient.updateMuseum(museum);
    }
}