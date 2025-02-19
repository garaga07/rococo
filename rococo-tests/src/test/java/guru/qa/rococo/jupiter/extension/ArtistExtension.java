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
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistClient artistClient = new ArtistDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artistAnno -> {
                    List<ArtistJson> result = new ArrayList<>();
                    String[] names = artistAnno.names();

                    for (int i = 0; i < artistAnno.count(); i++) {
                        String name = (i < names.length && !names[i].isEmpty()) ? names[i] : RandomDataUtils.randomArtistName();
                        String biography = RandomDataUtils.randomBiography();
                        String photo = RandomDataUtils.randomBase64Image();

                        result.add(new ArtistJson(null, name, biography, photo));
                    }

                    List<ArtistJson> createdArtists = artistClient.createArtists(result);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdArtists);
                });
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
    @SuppressWarnings("unchecked")
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<ArtistJson> artists = (List<ArtistJson>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class);

        if (artists == null || artists.isEmpty()) {
            throw new IllegalStateException("No artists were created in ArtistExtension");
        }

        Class<?> type = parameterContext.getParameter().getType();
        return type.isAssignableFrom(ArtistJson[].class) ? artists.toArray(new ArtistJson[0]) : artists.getFirst();
    }
}