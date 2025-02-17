package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.museum.CountryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface CountryRepository {
    @Nonnull
    Optional<CountryEntity> findById(UUID id);
}
