package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.impl.ArtistDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        Optional<Artist> artistAnno = context.getTestMethod()
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(Artist.class)));

        artistAnno.ifPresent(artist -> {
            List<ArtistJson> artistsToCreate = new ArrayList<>();
            int count = Math.max(artist.count(), 1);
            String[] names = artist.names();

            for (int i = 0; i < count; i++) {
                String name = (i < names.length && !names[i].isEmpty()) ? names[i] : RandomDataUtils.randomArtistName();
                String biography = RandomDataUtils.randomBiography();
                String photo = RandomDataUtils.randomBase64Image();

                artistsToCreate.add(new ArtistJson(null, name, biography, photo));
            }

            List<ArtistJson> createdArtists = artistClient.createArtists(artistsToCreate);
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
        return (List<ArtistJson>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class);
    }
}