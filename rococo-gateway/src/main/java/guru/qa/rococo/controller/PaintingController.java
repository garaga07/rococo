package guru.qa.rococo.controller;

import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.RestPaintingDataClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/painting")
public class PaintingController {

    private final RestPaintingDataClient restPaintingDataClient;

    @Autowired
    public PaintingController(RestPaintingDataClient restPaintingDataClient) {
        this.restPaintingDataClient = restPaintingDataClient;
    }

    @GetMapping("/{id}")
    public PaintingJson getPaintingById(@PathVariable UUID id) {
        return restPaintingDataClient.getPaintingById(id);
    }

    @GetMapping
    public Page<PaintingJson> getAllPaintings(Pageable pageable, @RequestParam(required = false) String title) {
        return restPaintingDataClient.getAllPaintings(pageable, title);
    }

    @GetMapping("/author/{authorId}")
    public Page<PaintingJson> getPaintingsByAuthorId(@PathVariable UUID authorId, Pageable pageable) {
        return restPaintingDataClient.getPaintingsByAuthorId(authorId, pageable);
    }

    @PostMapping
    public PaintingJson addPainting(@RequestBody PaintingJson painting) {
        return restPaintingDataClient.addPainting(painting);
    }

    @PatchMapping
    public PaintingJson updatePainting(@RequestBody PaintingJson painting) {
        return restPaintingDataClient.updatePainting(painting);
    }
}