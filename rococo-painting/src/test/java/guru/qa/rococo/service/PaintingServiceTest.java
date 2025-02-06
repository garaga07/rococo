package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.*;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class PaintingServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private RestArtistClient artistClient;

    @Mock
    private RestMuseumClient museumClient;

    @InjectMocks
    private PaintingService paintingService;

    private UUID paintingId;
    private UUID artistId;
    private UUID museumId;
    private PaintingEntity paintingEntity;
    private ArtistJson artistJson;
    private MuseumJson museumJson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paintingId = UUID.fromString("777e4567-e89b-12d3-a456-426614174000");
        artistId = UUID.fromString("666e4567-e89b-12d3-a456-426614174001");
        museumId = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");

        paintingEntity = new PaintingEntity();
        paintingEntity.setId(paintingId);
        paintingEntity.setTitle("The Swing");
        paintingEntity.setDescription("A famous Rococo painting");
        paintingEntity.setContent("data:image/jpeg;base64,/9j/4AAQSkZ".getBytes());
        paintingEntity.setArtist(artistId);
        paintingEntity.setMuseum(museumId);

        artistJson = new ArtistJson(artistId, "Jean-Honoré Fragonard", "Famous Rococo artist", null);
        museumJson = new MuseumJson(museumId, "Louvre Museum", "A museum in Paris", null, null);
    }

    @Test
    void getPaintingById_ShouldReturnPainting() {
        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);

        PaintingResponseJson result = paintingService.getPaintingById(paintingId);

        assertThat(result.id()).isEqualTo(paintingId);
        assertThat(result.title()).isEqualTo("The Swing");
        assertThat(result.artistJson().name()).isEqualTo("Jean-Honoré Fragonard");
        assertThat(result.museumJson().title()).isEqualTo("Louvre Museum");
    }

    @Test
    void getPaintingById_ShouldThrowNotFoundException() {
        when(paintingRepository.findById(paintingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.getPaintingById(paintingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Painting not found with id: " + paintingId);
    }

    @Test
    void getPaintingsByAuthorId_ShouldReturnPaintings() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> paintingPage = new PageImpl<>(List.of(paintingEntity), pageable, 1);
        when(paintingRepository.findAllByArtist(artistId, pageable)).thenReturn(paintingPage);
        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);

        Page<PaintingResponseJson> result = paintingService.getPaintingsByAuthorId(artistId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("The Swing");
    }

    @Test
    void getAllPainting_WithTitleFilter_ShouldReturnFilteredPaintings() {
        Pageable pageable = PageRequest.of(0, 10);
        String titleFilter = "The Swing";
        Page<PaintingEntity> paintingPage = new PageImpl<>(List.of(paintingEntity), pageable, 1);

        when(paintingRepository.searchPaintings(titleFilter, pageable)).thenReturn(paintingPage);
        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);

        Page<PaintingResponseJson> result = paintingService.getAllPainting(pageable, titleFilter);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("The Swing");
    }

    @Test
    void getAllPainting_WithoutTitleFilter_ShouldReturnAllPaintings() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> paintingPage = new PageImpl<>(List.of(paintingEntity), pageable, 1);

        when(paintingRepository.findAll(pageable)).thenReturn(paintingPage);
        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);

        Page<PaintingResponseJson> result = paintingService.getAllPainting(pageable, null);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("The Swing");
    }

    @Test
    void addPainting_ShouldThrowException_WhenArtistNotFound() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                null, "The Kiss", "A famous painting", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(UUID.randomUUID()), new MuseumRef(museumId)
        );

        when(artistClient.getArtistById(anyString())).thenThrow(new NotFoundException("Artist not found"));

        assertThatThrownBy(() -> paintingService.addPainting(requestJson))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Artist not found");
    }

    @Test
    void addPainting_ShouldThrowException_WhenMuseumNotFound() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                null, "The Kiss", "A famous painting", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(artistId), new MuseumRef(UUID.randomUUID())
        );

        when(museumClient.getMuseumById(anyString())).thenThrow(new NotFoundException("Museum not found"));

        assertThatThrownBy(() -> paintingService.addPainting(requestJson))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Museum not found");
    }

    @Test
    void updatePainting_ShouldUpdateAndReturnPainting() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                paintingId, "Updated Title", "Updated Description", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(artistId), new MuseumRef(museumId)
        );

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity);
        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);

        PaintingResponseJson result = paintingService.updatePainting(requestJson);

        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.description()).isEqualTo("Updated Description");
    }

    @Test
    void addPainting_ShouldSaveAndReturnPainting() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                null, "The Swing", "A famous Rococo painting", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(artistId), new MuseumRef(museumId)
        );

        when(artistClient.getArtistById(artistId.toString())).thenReturn(artistJson);
        when(museumClient.getMuseumById(museumId.toString())).thenReturn(museumJson);
        when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity);

        PaintingResponseJson result = paintingService.addPainting(requestJson);

        assertThat(result.title()).isEqualTo("The Swing");
        assertThat(result.artistJson().name()).isEqualTo("Jean-Honoré Fragonard");
        assertThat(result.museumJson().title()).isEqualTo("Louvre Museum");
    }

    @Test
    void updatePainting_ShouldThrowException_WhenIdIsNull() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                null, "Updated Title", "Updated Description", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(artistId), new MuseumRef(museumId)
        );

        assertThatThrownBy(() -> paintingService.updatePainting(requestJson))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id: ID картины обязателен для заполнения");
    }

    @Test
    void updatePainting_ShouldThrowException_WhenPaintingNotFound() {
        PaintingRequestJson requestJson = new PaintingRequestJson(
                paintingId, "Updated Title", "Updated Description", "data:image/jpeg;base64,/9j/4AAQSkZ",
                new ArtistRef(artistId), new MuseumRef(museumId)
        );

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paintingService.updatePainting(requestJson))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("id: Картина не найдена с id: " + paintingId);
    }
}