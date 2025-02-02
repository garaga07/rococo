package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    @Query("SELECT a FROM ArtistEntity a " +
            "WHERE LOWER(a.name) ILIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY a.name ASC")
    Page<ArtistEntity> searchArtists(@Param("name") String name, Pageable pageable);
}