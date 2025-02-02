package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
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
    public Page<PaintingResponseJson> getAllPaintings(Pageable pageable, @RequestParam(required = false) String title) {
        return paintingService.getAllPainting(pageable, title);
    }

    @GetMapping("/author/{authorId}")
    public Page<PaintingResponseJson> getPaintingsByAuthorId(@PathVariable UUID authorId, Pageable pageable) {
        return paintingService.getPaintingsByAuthorId(authorId, pageable);
    }

    @GetMapping("/{paintingId}")
    public PaintingResponseJson getPaintingById(@PathVariable UUID paintingId) {
        return paintingService.getPaintingById(paintingId);
    }

    @PostMapping
    public PaintingResponseJson addPainting(@RequestBody PaintingRequestJson paintingRequestJson) {
        return paintingService.addPainting(paintingRequestJson);
    }

    @PatchMapping
    public PaintingResponseJson updatePainting(@RequestBody PaintingRequestJson paintingRequestJson) {
        return paintingService.updatePainting(paintingRequestJson);
    }
}