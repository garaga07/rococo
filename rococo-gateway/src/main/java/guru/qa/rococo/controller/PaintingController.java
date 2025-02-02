package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
import guru.qa.rococo.service.api.RestPaintingDataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/painting")
@Validated
public class PaintingController {

    private final RestPaintingDataClient restPaintingDataClient;

    @Autowired
    public PaintingController(RestPaintingDataClient restPaintingDataClient) {
        this.restPaintingDataClient = restPaintingDataClient;
    }

    @GetMapping("/{id}")
    public PaintingResponseJson getPaintingById(@PathVariable UUID id) {
        return restPaintingDataClient.getPaintingById(id);
    }

    @GetMapping
    public Page<PaintingResponseJson> getAllPaintings(Pageable pageable, @RequestParam(required = false) String title) {
        return restPaintingDataClient.getAllPaintings(pageable, title);
    }

    @GetMapping("/author/{authorId}")
    public Page<PaintingResponseJson> getPaintingsByAuthorId(@PathVariable UUID authorId, Pageable pageable) {
        return restPaintingDataClient.getPaintingsByAuthorId(authorId, pageable);
    }

    @PostMapping
    public PaintingResponseJson addPainting(@Valid @RequestBody PaintingRequestJson painting) {
        return restPaintingDataClient.addPainting(painting);
    }

    @PatchMapping
    public PaintingResponseJson updatePainting(@Valid @RequestBody PaintingRequestJson painting) {
        return restPaintingDataClient.updatePainting(painting);
    }
}