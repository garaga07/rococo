package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

    Page<PaintingEntity> findAllByArtist(UUID artist, Pageable pageable);

    @Query("SELECT p FROM PaintingEntity p " +
            "WHERE LOWER(p.title) ILIKE LOWER(CONCAT('%', :title, '%')) " +
            "ORDER BY p.title ASC")
    Page<PaintingEntity> searchPaintings(@Param("title") String title, Pageable pageable);
}