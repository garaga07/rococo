package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class ArtistRepositoryHibernate implements ArtistRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.artistJdbcUrl());

    @Nonnull
    @Override
    public ArtistEntity create(ArtistEntity artist) {
        entityManager.joinTransaction();
        entityManager.persist(artist);
        return artist;
    }

    @Nonnull
    @Override
    public ArtistEntity update(ArtistEntity artist) {
        entityManager.joinTransaction();
        return entityManager.merge(artist);
    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(ArtistEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<ArtistEntity> findByName(String name) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT a FROM ArtistEntity a WHERE a.name =: name", ArtistEntity.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}