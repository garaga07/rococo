package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.model.PaintingJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Transactional(readOnly = true)
    public PaintingJson getPaintingById(UUID id) {
        return paintingRepository.findById(id)
                .map(PaintingJson::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Painting not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<PaintingJson> getPaintingByAuthorId(UUID authorId) {
        return paintingRepository.findAllByArtist(authorId)
                .stream()
                .map(PaintingJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaintingJson> getAllPainting() {
        return paintingRepository.findAll()
                .stream()
                .map(PaintingJson::fromEntity)
                .collect(Collectors.toList());
    }
}
