package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.MuseumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {
    @Query("SELECT m FROM MuseumEntity m " +
            "WHERE LOWER(m.title) ILIKE LOWER(CONCAT('%', :title, '%')) " +
            "ORDER BY m.title ASC")
    Page<MuseumEntity> searchMuseums(@Param("title") String title, Pageable pageable);
}