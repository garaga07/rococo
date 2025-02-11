package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.ArtistJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistClient {

    @Nonnull
    ArtistJson createArtist(ArtistJson artist);

    @Nonnull
    ArtistJson updateArtist(ArtistJson artist);

    @Nonnull
    Optional<ArtistJson> findArtistById(UUID id);

    @Nonnull
    Optional<ArtistJson> findArtistByName(String name);
}