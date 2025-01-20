package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final RestArtistClient artistClient;
    private final RestMuseumClient museumClient;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository,
                           RestArtistClient artistClient,
                           RestMuseumClient museumClient) {
        this.paintingRepository = paintingRepository;
        this.artistClient = artistClient;
        this.museumClient = museumClient;
    }

    @Transactional(readOnly = true)
    public PaintingJson getPaintingById(UUID id) {
        PaintingEntity paintingEntity = paintingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Painting not found with id: " + id));

        ArtistJson artist = artistClient.getArtistById(paintingEntity.getArtist().toString());
        MuseumJson museum = museumClient.getMuseumById(paintingEntity.getMuseum().toString());

        return PaintingJson.fromEntity(paintingEntity, artist, museum);
    }

    @Transactional(readOnly = true)
    public List<PaintingJson> getPaintingByAuthorId(UUID authorId) {
        List<PaintingEntity> paintings = paintingRepository.findAllByArtist(authorId);

        return paintings.stream()
                .map(entity -> {
                    ArtistJson artist = artistClient.getArtistById(entity.getArtist().toString());
                    MuseumJson museum = museumClient.getMuseumById(entity.getMuseum().toString());
                    return PaintingJson.fromEntity(entity, artist, museum);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaintingJson> getAllPainting() {
        List<PaintingEntity> paintings = paintingRepository.findAll();

        return paintings.stream()
                .map(entity -> {
                    ArtistJson artist = artistClient.getArtistById(entity.getArtist().toString());
                    MuseumJson museum = museumClient.getMuseumById(entity.getMuseum().toString());
                    return PaintingJson.fromEntity(entity, artist, museum);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PaintingJson addPainting(PaintingJson paintingJson) {
        // Проверка наличия артиста
        artistClient.getArtistById(paintingJson.artistJson().id().toString());

        // Проверка наличия музея
        museumClient.getMuseumById(paintingJson.museumJson().id().toString());

        // Если все проверки пройдены, сохраняем картину
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setTitle(paintingJson.title());
        if (isPhotoString(paintingJson.content())) {
            paintingEntity.setContent(paintingJson.content().getBytes(StandardCharsets.UTF_8));
        }
        paintingEntity.setArtist(paintingJson.artistJson().id());
        paintingEntity.setMuseum(paintingJson.museumJson().id());

        PaintingEntity savedPainting = paintingRepository.save(paintingEntity);

        // Получение данных артиста и музея для ответа
        ArtistJson artist = artistClient.getArtistById(savedPainting.getArtist().toString());
        MuseumJson museum = museumClient.getMuseumById(savedPainting.getMuseum().toString());

        return PaintingJson.fromEntity(savedPainting, artist, museum);
    }

    @Transactional
    public PaintingJson updatePainting(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = paintingRepository.findById(paintingJson.id())
                .orElseThrow(() -> new NotFoundException("Painting not found with id: " + paintingJson.id()));
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setTitle(paintingJson.title());
        if (isPhotoString(paintingJson.content())) {
            paintingEntity.setContent(paintingJson.content().getBytes(StandardCharsets.UTF_8));
        }
        paintingEntity.setArtist(paintingJson.artistJson().id());
        paintingEntity.setMuseum(paintingJson.museumJson().id());
        PaintingEntity savedPainting = paintingRepository.save(paintingEntity);
        ArtistJson artist = artistClient.getArtistById(savedPainting.getArtist().toString());
        MuseumJson museum = museumClient.getMuseumById(savedPainting.getMuseum().toString());
        return PaintingJson.fromEntity(savedPainting, artist, museum);
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}