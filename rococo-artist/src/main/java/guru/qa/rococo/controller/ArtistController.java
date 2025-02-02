package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/artist")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ArtistJson getArtistById(@PathVariable UUID id) {
        return artistService.getArtistById(id);
    }

    @GetMapping
    public Page<ArtistJson> getAllArtists(Pageable pageable, @RequestParam(required = false) String name) {
        return artistService.getAllArtists(pageable, name);
    }

    @PostMapping
    public ArtistJson addArtist(@RequestBody ArtistJson artist) {
        return artistService.addArtist(artist);
    }

    @PatchMapping
    public ArtistJson updateArtist(@RequestBody ArtistJson artist) {
        return artistService.updateArtist(artist);
    }
}