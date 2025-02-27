package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class MuseumRepositoryHibernate implements MuseumRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.museumJdbcUrl());

    @Nonnull
    @Override
    public MuseumEntity create(MuseumEntity museum) {
        entityManager.persist(museum);
        return museum;
    }

    @Nonnull
    @Override
    public MuseumEntity update(MuseumEntity museum) {
        return entityManager.merge(museum);
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(MuseumEntity.class, id));
    }

    @Nonnull
    @Override
    public Optional<MuseumEntity> findByTitle(String title) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT m FROM MuseumEntity m WHERE m.title = :title", MuseumEntity.class)
                            .setParameter("title", title)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(@Nonnull MuseumEntity museum) {
        MuseumEntity managedMuseum = entityManager.contains(museum) ? museum : entityManager.merge(museum);
        entityManager.remove(managedMuseum);
        entityManager.flush();
    }
}