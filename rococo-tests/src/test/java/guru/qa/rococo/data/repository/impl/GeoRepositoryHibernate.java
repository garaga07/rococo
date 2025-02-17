package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.GeoEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class GeoRepositoryHibernate implements GeoRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.museumJdbcUrl());

    @Nonnull
    @Override
    public GeoEntity create(GeoEntity geo) {
        entityManager.joinTransaction();
        entityManager.persist(geo);
        return geo;
    }

    @Nonnull
    @Override
    public Optional<GeoEntity> findByCityAndCountry(UUID countryId, String city) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                                    "SELECT g FROM GeoEntity g WHERE g.city = :city AND g.country.id = :countryId",
                                    GeoEntity.class
                            )
                            .setParameter("city", city)
                            .setParameter("countryId", countryId)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
