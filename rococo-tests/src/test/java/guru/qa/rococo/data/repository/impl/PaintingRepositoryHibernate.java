package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class PaintingRepositoryHibernate implements PaintingRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.paintingJdbcUrl());

    @Nonnull
    @Override
    public PaintingEntity create(PaintingEntity painting) {
        entityManager.persist(painting);
        return painting;
    }

    @Nonnull
    @Override
    public PaintingEntity update(PaintingEntity painting) {
        return entityManager.merge(painting);
    }

    @Nonnull
    @Override
    public Optional<PaintingEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(PaintingEntity.class, id));
    }

    @Override
    public void deleteById(UUID id) {
        PaintingEntity painting = entityManager.find(PaintingEntity.class, id);
        if (painting != null) {
            entityManager.remove(painting);
            entityManager.flush();
        }
    }
}