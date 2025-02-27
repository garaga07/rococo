package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class ArtistDbClient implements ArtistClient {

    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.artistJdbcUrl());

    @Step("Create artist using SQL")
    @Nonnull
    @Override
    public ArtistJson createArtist(ArtistJson artist) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> ArtistJson.fromEntity(
                                artistRepository.create(
                                        ArtistEntity.fromJson(artist)
                                )
                        )
                )
        );
    }

    @Step("Create multiple artists using SQL")
    @Nonnull
    @Override
    public List<ArtistJson> createArtists(List<ArtistJson> artists) {
        return requireNonNull(
                xaTransactionTemplate.execute(() ->
                        artists.stream()
                                .map(artist -> ArtistJson.fromEntity(
                                        artistRepository.create(ArtistEntity.fromJson(artist))
                                ))
                                .collect(Collectors.toList())
                )
        );
    }

    @Step("Update artist using SQL")
    @Nonnull
    @Override
    public ArtistJson updateArtist(ArtistJson artist) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> ArtistJson.fromEntity(
                                artistRepository.update(
                                        ArtistEntity.fromJson(artist)
                                )
                        )
                )
        );
    }

    @Step("Find artist by id using SQL")
    @Nonnull
    @Override
    public Optional<ArtistJson> findArtistById(UUID id) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                artistRepository.findById(id).map(ArtistJson::fromEntity)
        ));
    }

    @Step("Find artist by name using SQL")
    @Nonnull
    @Override
    public Optional<ArtistJson> findArtistByName(String name) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                artistRepository.findByName(name).map(ArtistJson::fromEntity)
        ));
    }

    @Step("Delete artist by id using SQL")
    @Override
    public void deleteArtistById(UUID id) {
        requireNonNull(id, "Artist ID must not be null");
        xaTransactionTemplate.execute(() -> {
            artistRepository.deleteById(id);
            return null;
        });
    }
}