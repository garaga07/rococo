package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.api.RestArtistDataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/artist")
@Validated
public class ArtistController {

    private final RestArtistDataClient restArtistDataClient;

    @Autowired
    public ArtistController(RestArtistDataClient restArtistDataClient) {
        this.restArtistDataClient = restArtistDataClient;
    }

    @GetMapping("/{id}")
    public ArtistJson getArtistById(@PathVariable UUID id) {
        return restArtistDataClient.getArtistById(id);
    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(Pageable pageable, @RequestParam(required = false) String name) {
        return restArtistDataClient.getAllArtists(pageable, name);
    }

    @PostMapping
    public ArtistJson addArtist(@Valid @RequestBody ArtistJson artist) {
        return restArtistDataClient.saveArtist(artist);
    }

    @PatchMapping
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artist) {
        return restArtistDataClient.updateArtistInfo(artist);
    }
}