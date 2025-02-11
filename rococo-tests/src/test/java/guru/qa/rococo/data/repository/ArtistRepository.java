package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.artist.ArtistEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistRepository {

    @Nonnull
    ArtistEntity create(ArtistEntity artist);

    @Nonnull
    ArtistEntity update(ArtistEntity artist);

    @Nonnull
    Optional<ArtistEntity> findById(UUID id);

    @Nonnull
    Optional<ArtistEntity> findByName(String name);
}