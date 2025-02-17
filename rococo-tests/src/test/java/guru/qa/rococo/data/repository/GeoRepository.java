package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.museum.GeoEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface GeoRepository {

    @Nonnull
    GeoEntity create(GeoEntity geo);

    @Nonnull
    Optional<GeoEntity> findByCityAndCountry(UUID countryId, String city);
}