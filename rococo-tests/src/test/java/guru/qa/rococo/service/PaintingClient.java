package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingClient {

    @Nonnull
    PaintingJson create(PaintingJson painting);

    @Nonnull
    List<PaintingJson> createPaintings(List<PaintingJson> paintings);

    @Nonnull
    PaintingJson update(PaintingJson painting);

    @Nonnull
    Optional<PaintingJson> findById(UUID id);

    void deleteById(UUID id);
}