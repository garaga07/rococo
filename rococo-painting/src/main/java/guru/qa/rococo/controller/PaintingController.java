package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/painting")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping()
    public List<PaintingJson> getAllPainting() {
        return paintingService.getAllPainting();
    }

    @GetMapping("/author/{authorId}")
    public List<PaintingJson> getPaintingByAuthorId(@PathVariable UUID authorId) {
        return paintingService.getPaintingByAuthorId(authorId);
    }

    @GetMapping("/{paintingId}")
    public PaintingJson getPaintingById(@PathVariable UUID paintingId) {
        return paintingService.getPaintingById(paintingId);
    }

    @PostMapping
    public PaintingJson addPainting(@RequestBody PaintingJson paintingJson) {
        return paintingService.addPainting(paintingJson);
    }

    @PatchMapping
    public PaintingJson updatePainting(@RequestBody PaintingJson paintingJson) {
        return paintingService.updatePainting(paintingJson);
    }
}