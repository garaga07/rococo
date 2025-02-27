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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                                        MuseumEntity.fromJson(museum, createGeo(museum.geo()))
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
                xaTransactionTemplate.execute(() ->
                        museums.stream()
                                .map(museum -> MuseumJson.fromEntity(
                                        museumRepository.create(
                                                MuseumEntity.fromJson(museum, createGeo(museum.geo()))
                                        )
                                ))
                                .collect(Collectors.toList())
                )
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
                                        MuseumEntity.fromJson(museum, createGeo(museum.geo()))
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

    @Step("Delete museum by id using SQL")
    @Override
    public void deleteMuseumById(UUID id) {
        xaTransactionTemplate.execute(() -> {
            museumRepository.findById(id).ifPresent(museum -> {
                museumRepository.delete(museum);
                geoRepository.findByCityAndCountry(museum.getGeo().getCountry().getId(), museum.getGeo().getCity())
                        .ifPresent(geoRepository::delete);
            });
            return null;
        });
    }

    private GeoEntity createGeo(GeoJson geoJson) {
        CountryEntity countryEntity = countryRepository.findById(geoJson.country().id())
                .orElseThrow(() -> new IllegalStateException("Country not found: " + geoJson.country().id()));
        return geoRepository.create(GeoEntity.fromJson(geoJson, countryEntity));
    }
}