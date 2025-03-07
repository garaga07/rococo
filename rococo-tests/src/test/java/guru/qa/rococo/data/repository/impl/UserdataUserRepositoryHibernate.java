package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.userdata.UserdataEntity;
import guru.qa.rococo.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Nonnull
    @Override
    public UserdataEntity create(UserdataEntity user) {
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    @Override
    public UserdataEntity update(UserdataEntity user) {
        return entityManager.merge(user);
    }

    @Nonnull
    @Override
    public Optional<UserdataEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserdataEntity.class, id));
    }

    @Nonnull
    @Override
    public Optional<UserdataEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM UserdataEntity u WHERE u.username = :username", UserdataEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(UserdataEntity user) {
        UserdataEntity managedUser = entityManager.contains(user) ? user : entityManager.merge(user);
        entityManager.remove(managedUser);
        entityManager.flush();
    }
}