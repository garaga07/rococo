package guru.qa.rococo.controller;

import guru.qa.rococo.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<String> getAllArtist() throws IOException {
        return ResponseEntity.ok(artistService.getAllArtistJson());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getArtistById(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }
}