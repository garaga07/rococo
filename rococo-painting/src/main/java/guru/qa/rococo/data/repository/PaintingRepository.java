package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.PaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    List<PaintingEntity> findAllByArtist(UUID artist);
}