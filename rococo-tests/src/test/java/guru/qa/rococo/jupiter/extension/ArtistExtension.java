package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArtistExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Logger LOGGER = Logger.getLogger(ArtistExtension.class.getName());
    private final ArtistClient artistClient = new ArtistDbClient();

    // Потокобезопасное хранилище данных
    private static final ThreadLocal<List<ArtistJson>> artistStore = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {
                    List<ArtistJson> artistsToCreate = new ArrayList<>();
                    String[] names = artistAnno.names();

                    for (int i = 0; i < artistAnno.count(); i++) {
                        String name = (i < names.length && !names[i].isEmpty()) ? names[i] : RandomDataUtils.randomArtistName();
                        String biography = RandomDataUtils.randomBiography();
                        String photo = RandomDataUtils.randomBase64Image();
                        artistsToCreate.add(new ArtistJson(null, name, biography, photo));
                    }

                    List<ArtistJson> createdArtists = artistClient.createArtists(artistsToCreate);
                    artistStore.set(createdArtists);

                    LOGGER.info(() -> "Created artists: " + createdArtists);
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        List<ArtistJson> createdArtists = artistStore.get();
        if (createdArtists != null) {
            for (ArtistJson artist : createdArtists) {
                try {
                    artistClient.deleteArtistById(artist.id());
                    LOGGER.info(() -> "Deleted artist: " + artist.id());
                } catch (Exception e) {
                    LOGGER.severe(() -> "Failed to delete artist: " + artist.id() + " due to " + e.getMessage());
                }
            }
        }
        artistStore.remove(); // Удаляем данные потока
    }

    public static ArtistJson getArtistForTest() {
        List<ArtistJson> artists = artistStore.get();
        if (artists == null || artists.isEmpty()) {
            throw new IllegalStateException("No artists were created in ArtistExtension");
        }
        return artists.getFirst();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        boolean hasArtistAnnotation = extensionContext.getTestMethod()
                .map(m -> m.isAnnotationPresent(Artist.class))
                .or(() -> extensionContext.getTestClass().map(c -> c.isAnnotationPresent(Artist.class)))
                .orElse(false);

        Class<?> type = parameterContext.getParameter().getType();
        return hasArtistAnnotation && (type.isAssignableFrom(ArtistJson.class) || type.isAssignableFrom(ArtistJson[].class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<ArtistJson> artists = artistStore.get();

        if (artists == null || artists.isEmpty()) {
            throw new IllegalStateException("No artists were created in ArtistExtension");
        }

        Class<?> type = parameterContext.getParameter().getType();
        return type.isAssignableFrom(ArtistJson[].class) ? artists.toArray(new ArtistJson[0]) : artists.getFirst();
    }
}