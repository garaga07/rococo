package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.model.ArtistJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
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
                .orElseThrow(() -> new IllegalArgumentException("Artist not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ArtistJson> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(ArtistJson::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ArtistJson addArtist(ArtistJson artist) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setName(artist.name());
        artistEntity.setBiography(artist.biography());
        if (isPhotoString(artist.photo())) {
            artistEntity.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
        }
        ArtistEntity savedArtist = artistRepository.save(artistEntity);
        return ArtistJson.fromEntity(savedArtist);
    }

    @Transactional
    public ArtistJson updateArtist(ArtistJson artist) {
        Optional<ArtistEntity> artistById = artistRepository.findById(artist.id());
        if (artistById.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can`t find artist by id: " + artist.id());
        } else {
            final ArtistEntity artistEntity = artistById.get();
            artistEntity.setBiography(artist.biography());
            artistEntity.setName(artist.name());
            if (isPhotoString(artist.photo())) {
                artistEntity.setPhoto(artist.photo().getBytes(StandardCharsets.UTF_8));
            }
            ArtistEntity savedArtist = artistRepository.save(artistEntity);
            return ArtistJson.fromEntity(savedArtist);
        }
    }

    private boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}