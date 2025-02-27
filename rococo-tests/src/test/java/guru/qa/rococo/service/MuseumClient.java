package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.MuseumJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumClient {

    @Nonnull
    MuseumJson createMuseum(MuseumJson museum);

    @Nonnull
    List<MuseumJson> createMuseums(List<MuseumJson> museums);

    @Nonnull
    MuseumJson updateMuseum(MuseumJson museum);

    @Nonnull
    Optional<MuseumJson> findMuseumById(UUID id);

    @Nonnull
    Optional<MuseumJson> findMuseumByTitle(String title);

    void deleteMuseumById(UUID id);
}
