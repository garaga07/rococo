package guru.qa.rococo.controller;

import guru.qa.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {
    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping()
    public ResponseEntity<String> getAllPainting() throws IOException {
        return ResponseEntity.ok(paintingService.getAllPaintingJson());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<String> getPaintingByAuthor(@PathVariable String authorId) throws IOException {
        return ResponseEntity.ok(paintingService.getPaintingByAuthorJson(authorId));
    }

    @GetMapping("/{paintingId}")
    public ResponseEntity<String> getPaintingById(@PathVariable String paintingId) throws IOException {
        return ResponseEntity.ok(paintingService.getPaintingByIdJson(paintingId));
    }
}
