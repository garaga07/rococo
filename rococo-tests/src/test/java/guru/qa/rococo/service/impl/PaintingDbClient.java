package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.repository.impl.PaintingRepositoryHibernate;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.PaintingJson;

import guru.qa.rococo.service.PaintingClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class PaintingDbClient implements PaintingClient {

    private static final Config CFG = Config.getInstance();
    private final PaintingRepository paintingRepository = new PaintingRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(CFG.paintingJdbcUrl());

    @Step("Create painting using SQL")
    @Nonnull
    @Override
    public PaintingJson create(PaintingJson painting) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> PaintingJson.fromEntity(
                                paintingRepository.create(
                                        PaintingEntity.fromJson(painting)
                                )
                        )
                )
        );
    }

    @Step("Create multiple paintings using SQL")
    @Nonnull
    @Override
    public List<PaintingJson> createPaintings(List<PaintingJson> paintings) {
        return requireNonNull(
                xaTransactionTemplate.execute(() ->
                        paintings.stream()
                                .map(painting -> PaintingJson.fromEntity(
                                        paintingRepository.create(PaintingEntity.fromJson(painting))
                                ))
                                .collect(Collectors.toList())
                )
        );
    }

    @Step("Update painting using SQL")
    @Nonnull
    @Override
    public PaintingJson update(PaintingJson painting) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> PaintingJson.fromEntity(
                                paintingRepository.update(
                                        PaintingEntity.fromJson(painting)
                                )
                        )
                )
        );
    }

    @Step("Find painting by id using SQL")
    @Nonnull
    @Override
    public Optional<PaintingJson> findById(UUID id) {
        return requireNonNull(xaTransactionTemplate.execute(() ->
                paintingRepository.findById(id).map(PaintingJson::fromEntity)
        ));
    }

    @Step("Delete painting by id using SQL")
    @Override
    public void deleteById(UUID id) {
        requireNonNull(id, "Painting ID must not be null");
        xaTransactionTemplate.execute(() -> {
            paintingRepository.deleteById(id);
            return null;
        });
    }
}