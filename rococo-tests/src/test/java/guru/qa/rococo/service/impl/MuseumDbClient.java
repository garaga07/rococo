package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.CountryEntity;
import guru.qa.rococo.data.entity.museum.GeoEntity;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.impl.CountryRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class MuseumDbClient implements MuseumClient {

    private static final Config CFG = Config.getInstance();
    private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    private final GeoRepository geoRepository = new GeoRepositoryHibernate();
    private final CountryRepository countryRepository = new CountryRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.museumJdbcUrl());

    @Step("Create museum using SQL")
    @Nonnull
    @Override
    public MuseumJson createMuseum(MuseumJson museum) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> MuseumJson.fromEntity(
                                museumRepository.create(
                                        MuseumEntity.fromJson(museum, ensureGeoExists(museum.geo()))
                                )
                        )
                )
        );
    }

    @Step("Create multiple museums using SQL")
    @Nonnull
    @Override
    public List<MuseumJson> createMuseums(List<MuseumJson> museums) {
        return requireNonNull(
                xaTransactionTemplate.execute(() -> {
                    List<MuseumJson> createdMuseums = new ArrayList<>();
                    for (MuseumJson museum : museums) {
                        createdMuseums.add(
                                MuseumJson.fromEntity(
                                        museumRepository.create(
                                                MuseumEntity.fromJson(museum, ensureGeoExists(museum.geo()))
                                        )
                                )
                        );
                    }
                    return createdMuseums;
                })
        );
    }

    @Step("Update museum using SQL")
    @Nonnull
    @Override
    public MuseumJson updateMuseum(MuseumJson museum) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> MuseumJson.fromEntity(
                                museumRepository.update(
                                        MuseumEntity.fromJson(museum, ensureGeoExists(museum.geo()))
                                )
                        )
                )
        );
    }

    @Step("Find museum by id using SQL")
    @Nonnull
    @Override
    public Optional<MuseumJson> findMuseumById(UUID id) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                museumRepository.findById(id).map(MuseumJson::fromEntity)
        ));
    }

    @Step("Find museum by title using SQL")
    @Nonnull
    @Override
    public Optional<MuseumJson> findMuseumByTitle(String title) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                museumRepository.findByTitle(title).map(MuseumJson::fromEntity)
        ));
    }

    private GeoEntity ensureGeoExists(GeoJson geoJson) {
        UUID countryId = geoJson.country().id();
        String city = geoJson.city();

        // Проверяем, существует ли страна
        CountryEntity countryEntity = countryRepository.findById(countryId)
                .orElseThrow(() -> new IllegalStateException("Country not found: " + countryId));

        // Проверяем, существуют ли гео-данные и если отсутствуют создаем
        return geoRepository.findByCityAndCountry(countryId, city)
                .orElseGet(() -> geoRepository.create(GeoEntity.fromJson(geoJson, countryEntity)));
    }
}