package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingRequestJson;
import guru.qa.rococo.model.PaintingResponseJson;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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
    public PaintingResponseJson getPaintingById(UUID id) {
        PaintingEntity paintingEntity = paintingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Painting not found with id: " + id));

        ArtistJson artist = artistClient.getArtistById(paintingEntity.getArtist().toString());
        MuseumJson museum = museumClient.getMuseumById(paintingEntity.getMuseum().toString());

        return PaintingResponseJson.fromEntity(paintingEntity, artist, museum);
    }

    @Transactional(readOnly = true)
    public Page<PaintingResponseJson> getPaintingsByAuthorId(UUID authorId, Pageable pageable) {
        // Проверяем существование художника перед поиском картин
        artistClient.getArtistById(authorId.toString());

        Page<PaintingEntity> paintings = paintingRepository.findAllByArtist(authorId, pageable);

        List<PaintingResponseJson> paintingResponseJsons = paintings.stream()
                .map(entity -> {
                    ArtistJson artist = artistClient.getArtistById(entity.getArtist().toString());
                    MuseumJson museum = museumClient.getMuseumById(entity.getMuseum().toString());
                    return PaintingResponseJson.fromEntity(entity, artist, museum);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(paintingResponseJsons, pageable, paintings.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<PaintingResponseJson> getAllPainting(Pageable pageable, String title) {
        Page<PaintingEntity> paintings;

        if (title != null && !title.isBlank()) {
            String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8).trim();
            paintings = paintingRepository.searchPaintings(decodedTitle, pageable);
        } else {
            paintings = paintingRepository.findAll(pageable);
        }

        List<PaintingResponseJson> paintingResponseJsons = paintings.stream()
                .map(entity -> {
                    ArtistJson artist = artistClient.getArtistById(entity.getArtist().toString());
                    MuseumJson museum = museumClient.getMuseumById(entity.getMuseum().toString());
                    return PaintingResponseJson.fromEntity(entity, artist, museum);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(paintingResponseJsons, pageable, paintings.getTotalElements());
    }

    @Transactional
    public PaintingResponseJson addPainting(PaintingRequestJson paintingRequestJson) {
        // Проверка наличия артиста
        artistClient.getArtistById(paintingRequestJson.artist().id().toString());

        // Проверка наличия музея
        museumClient.getMuseumById(paintingRequestJson.museum().id().toString());

        // Если все проверки пройдены, сохраняем картину
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setDescription(paintingRequestJson.description().trim());
        paintingEntity.setTitle(paintingRequestJson.title().trim());
        if (isPhotoString(paintingRequestJson.content())) {
            paintingEntity.setContent(paintingRequestJson.content().getBytes(StandardCharsets.UTF_8));
        }
        paintingEntity.setArtist(paintingRequestJson.artist().id());
        paintingEntity.setMuseum(paintingRequestJson.museum().id());

        PaintingEntity savedPainting = paintingRepository.save(paintingEntity);

        // Получение данных артиста и музея для ответа
        ArtistJson artist = artistClient.getArtistById(savedPainting.getArtist().toString());
        MuseumJson museum = museumClient.getMuseumById(savedPainting.getMuseum().toString());

        return PaintingResponseJson.fromEntity(savedPainting, artist, museum);
    }

    @Transactional
    public PaintingResponseJson updatePainting(PaintingRequestJson paintingRequestJson) {
        if (paintingRequestJson.id() == null) {
            throw new BadRequestException("id: ID картины обязателен для заполнения");
        }

        PaintingEntity paintingEntity = paintingRepository.findById(paintingRequestJson.id())
                .orElseThrow(() -> new NotFoundException("id: Картина не найдена с id: " + paintingRequestJson.id()));

        paintingEntity.setDescription(paintingRequestJson.description().trim());
        paintingEntity.setTitle(paintingRequestJson.title().trim());

        if (isPhotoString(paintingRequestJson.content())) {
            paintingEntity.setContent(paintingRequestJson.content().getBytes(StandardCharsets.UTF_8));
        }

        paintingEntity.setArtist(paintingRequestJson.artist().id());
        paintingEntity.setMuseum(paintingRequestJson.museum().id());

        PaintingEntity savedPainting = paintingRepository.save(paintingEntity);

        ArtistJson updatedArtist = artistClient.getArtistById(savedPainting.getArtist().toString());
        MuseumJson updatedMuseum = museumClient.getMuseumById(savedPainting.getMuseum().toString());

        return PaintingResponseJson.fromEntity(savedPainting, updatedArtist, updatedMuseum);
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}