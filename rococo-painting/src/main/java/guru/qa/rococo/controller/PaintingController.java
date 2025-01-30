package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/painting")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(Pageable pageable, @RequestParam(required = false) String title) {
        return paintingService.getAllPainting(pageable, title);
    }

    @GetMapping("/author/{authorId}")
    public Page<PaintingJson> getPaintingsByAuthorId(@PathVariable UUID authorId, Pageable pageable) {
        return paintingService.getPaintingsByAuthorId(authorId, pageable);
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