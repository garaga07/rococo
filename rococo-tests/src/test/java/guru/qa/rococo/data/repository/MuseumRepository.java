package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.museum.MuseumEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumRepository {

    @Nonnull
    MuseumEntity create(MuseumEntity museum);

    @Nonnull
    MuseumEntity update(MuseumEntity museum);

    @Nonnull
    Optional<MuseumEntity> findById(UUID id);

    @Nonnull
    Optional<MuseumEntity> findByTitle(String title);

    void delete(@Nonnull MuseumEntity museum);
}