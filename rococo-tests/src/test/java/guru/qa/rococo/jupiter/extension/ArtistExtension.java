package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        Optional<Artist> artistAnno = context.getTestMethod().flatMap(m -> Optional.ofNullable(m.getAnnotation(Artist.class)));

        artistAnno.ifPresent(artist -> {
            int count = Math.max(artist.count(), 1);
            List<ArtistJson> createdArtists = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                String name = artist.name().isEmpty() ? RandomDataUtils.randomArtistName() : artist.name();
                String biography = artist.biography().isEmpty() ? RandomDataUtils.randomBiography() : artist.biography();
                String photo = artist.photo().isEmpty() ? RandomDataUtils.randomBase64Image() : artist.photo();

                ArtistJson artistJson = new ArtistJson(UUID.randomUUID(), name, biography, photo);
                createdArtists.add(artistClient.createArtist(artistJson));
            }

            context.getStore(NAMESPACE).put(context.getUniqueId(), createdArtists);
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ArtistJson> resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return (List<ArtistJson>) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
    }
}