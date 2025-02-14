package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
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

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional(readOnly = true)
    public ArtistJson getArtistById(UUID id) {
        return artistRepository.findById(id)
                .map(ArtistJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("id: Художник не найден с id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ArtistJson> getAllArtists(Pageable pageable, String name) {
        Page<ArtistEntity> artists;

        if (name != null && !name.isBlank()) {
            String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8).trim();
            artists = artistRepository.searchArtists(decodedName, pageable);
        } else {
            artists = artistRepository.findAll(pageable);
        }

        List<ArtistJson> artistJsons = artists.getContent()
                .stream()
                .map(ArtistJson::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(artistJsons, pageable, artists.getTotalElements());
    }

    @Transactional
    public ArtistJson addArtist(ArtistJson artist) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setName(artist.name().trim());
        artistEntity.setBiography(artist.biography().trim());
        if (isPhotoString(artist.photo())) {
            artistEntity.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
        }
        ArtistEntity savedArtist = artistRepository.save(artistEntity);
        return ArtistJson.fromEntity(savedArtist);
    }

    @Transactional
    public ArtistJson updateArtist(ArtistJson artist) {
        if (artist.id() == null) {
            throw new BadRequestException("id: ID художника обязателен для заполнения");
        }
        ArtistEntity artistEntity = artistRepository.findById(artist.id())
                .orElseThrow(() -> new NotFoundException("id: Художник не найден с id: " + artist.id()));

        artistEntity.setBiography(artist.biography().trim());
        artistEntity.setName(artist.name().trim());
        if (isPhotoString(artist.photo())) {
            artistEntity.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
        }
        ArtistEntity savedArtist = artistRepository.save(artistEntity);
        return ArtistJson.fromEntity(savedArtist);
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}