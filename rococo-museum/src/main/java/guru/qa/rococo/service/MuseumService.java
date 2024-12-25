package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.model.MuseumJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuseumService {

    private final MuseumRepository museumRepository;

    @Autowired
    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Transactional(readOnly = true)
    public MuseumJson getMuseumById(UUID id) {
        return museumRepository.findById(id)
                .map(MuseumJson::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Museum not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MuseumJson> getAllMuseums() {
        return museumRepository.findAll()
                .stream()
                .map(MuseumJson::fromEntity)
                .collect(Collectors.toList());
    }
}