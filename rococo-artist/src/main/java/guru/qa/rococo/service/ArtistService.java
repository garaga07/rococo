package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.model.ArtistJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new IllegalArgumentException("Artist not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ArtistJson> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(ArtistJson::fromEntity)
                .collect(Collectors.toList());
    }
}