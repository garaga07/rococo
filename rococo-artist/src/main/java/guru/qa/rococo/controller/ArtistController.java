package guru.qa.rococo.controller;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/all")
    public List<ArtistJson> getAllArtists() {
        return artistService.getAllArtists();
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