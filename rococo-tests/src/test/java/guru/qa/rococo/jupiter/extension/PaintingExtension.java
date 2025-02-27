package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.service.impl.PaintingDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class PaintingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Logger LOGGER = Logger.getLogger(PaintingExtension.class.getName());
    private final PaintingClient paintingClient = new PaintingDbClient();
    private final ArtistClient artistClient = new ArtistDbClient();
    private final MuseumClient museumClient = new MuseumDbClient();

    private static final ThreadLocal<List<PaintingJson>> paintingStore = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<UUID> artistStore = new ThreadLocal<>();
    private static final ThreadLocal<UUID> museumStore = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(paintingAnno -> {
                    List<PaintingJson> paintings = new ArrayList<>();
                    String[] titles = paintingAnno.titles();

                    // Создаем одного художника и один музей (с Geo) на все картины
                    ArtistJson artist = artistClient.createArtist(
                            new ArtistJson(null, RandomDataUtils.randomArtistName(), RandomDataUtils.randomBiography(), RandomDataUtils.randomBase64Image())
                    );

                    MuseumJson museum = museumClient.createMuseum(
                            new MuseumJson(null, RandomDataUtils.randomMuseumTitle(), RandomDataUtils.randomDescription(), RandomDataUtils.randomBase64Image(), RandomDataUtils.randomGeoJson())
                    );

                    // Сохраняем ID в ThreadLocal
                    artistStore.set(artist.id());
                    museumStore.set(museum.id());

                    for (int i = 0; i < paintingAnno.count(); i++) {
                        String title = (i < titles.length && !titles[i].isEmpty()) ? titles[i] : RandomDataUtils.randomPaintingTitle();
                        paintings.add(new PaintingJson(null, title, RandomDataUtils.randomDescription(), RandomDataUtils.randomBase64Image(), artist.id(), museum.id()));
                    }

                    paintingStore.set(paintingClient.createPaintings(paintings));
                    LOGGER.info(() -> "Created paintings: " + paintingStore.get());
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // Удаление художника
        UUID artistId = artistStore.get();
        if (artistId != null) {
            try {
                artistClient.deleteArtistById(artistId);
                LOGGER.info(() -> "Deleted artist: " + artistId);
            } catch (Exception e) {
                LOGGER.severe(() -> "Failed to delete artist: " + artistId + " due to " + e.getMessage());
            }
        }

        // Удаление музея и Geo
        UUID museumId = museumStore.get();
        if (museumId != null) {
            try {
                museumClient.deleteMuseumById(museumId);
                LOGGER.info(() -> "Deleted museum: " + museumId);
            } catch (Exception e) {
                LOGGER.severe(() -> "Failed to delete museum: " + museumId + " due to " + e.getMessage());
            }
        }

        // Удаление картин
        paintingStore.get().forEach(painting -> {
            try {
                paintingClient.deleteById(painting.id());
                LOGGER.info(() -> "Deleted painting: " + painting.id());
            } catch (Exception e) {
                LOGGER.severe(() -> "Failed to delete painting: " + painting.id() + " due to " + e.getMessage());
            }
        });

        // Очистка ThreadLocal
        artistStore.remove();
        museumStore.remove();
        paintingStore.remove();
    }

    public static PaintingJson getPaintingForTest() {
        List<PaintingJson> paintings = paintingStore.get();
        if (paintings == null || paintings.isEmpty()) {
            throw new IllegalStateException("No paintings were created in PaintingExtension");
        }
        return paintings.getFirst();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        boolean hasPaintingAnnotation = extensionContext.getTestMethod()
                .map(m -> m.isAnnotationPresent(Painting.class))
                .or(() -> extensionContext.getTestClass().map(c -> c.isAnnotationPresent(Painting.class)))
                .orElse(false);

        Class<?> type = parameterContext.getParameter().getType();
        return hasPaintingAnnotation && (type == PaintingJson.class || type == PaintingJson[].class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<PaintingJson> paintings = paintingStore.get();
        if (paintings == null || paintings.isEmpty()) {
            throw new IllegalStateException("No paintings were created in PaintingExtension");
        }

        Class<?> type = parameterContext.getParameter().getType();
        return type == PaintingJson[].class ? paintings.toArray(new PaintingJson[0]) : paintings.getFirst();
    }
}