package guru.qa.rococo.controller;

import guru.qa.rococo.service.ArtistService;
import guru.qa.rococo.service.MuseumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/museum")
public class MuseumController {
    private final MuseumService museumService;

    @Autowired
    public MuseumController(MuseumService museumService) {
        this.museumService = museumService;
    }

    @GetMapping()
    public ResponseEntity<String> getAllMuseum() throws IOException {
        return ResponseEntity.ok(museumService.getAllMuseumJson());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getMuseumById(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(museumService.getMuseumById(id));
    }
}