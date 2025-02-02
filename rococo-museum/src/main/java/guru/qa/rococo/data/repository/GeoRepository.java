package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.GeoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GeoRepository extends JpaRepository<GeoEntity, UUID> {
    @Query("SELECT g FROM GeoEntity g WHERE LOWER(g.city) = LOWER(:city) AND g.country.id = :countryId")
    GeoEntity findByCityAndCountryId(@Param("city") String city, @Param("countryId") UUID countryId);
}