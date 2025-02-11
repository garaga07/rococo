package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class ArtistDbClient implements ArtistClient {

    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.artistJdbcUrl());

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

    @Nonnull
    @Override
    public Optional<ArtistJson> findArtistById(UUID id) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                artistRepository.findById(id).map(ArtistJson::fromEntity)
        ));
    }

    @Nonnull
    @Override
    public Optional<ArtistJson> findArtistByName(String name) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                artistRepository.findByName(name).map(ArtistJson::fromEntity)
        ));
    }
}