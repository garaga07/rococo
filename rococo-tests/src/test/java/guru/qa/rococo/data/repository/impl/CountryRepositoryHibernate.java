package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class CountryRepositoryHibernate implements CountryRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.museumJdbcUrl());

    @Nonnull
    @Override
    public Optional<CountryEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(CountryEntity.class, id));
    }
}