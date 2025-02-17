package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        Optional<Museum> museumAnno = context.getTestMethod()
                .flatMap(m -> Optional.ofNullable(m.getAnnotation(Museum.class)));

        museumAnno.ifPresent(museum -> {
            List<MuseumJson> museumsToCreate = new ArrayList<>();
            int count = Math.max(museum.count(), 1);
            String[] titles = museum.titles();

            for (int i = 0; i < count; i++) {
                String title = (i < titles.length && !titles[i].isEmpty()) ? titles[i] : RandomDataUtils.randomMuseumTitle();
                String description = RandomDataUtils.randomMuseumDescription();
                String photo = RandomDataUtils.randomBase64Image();
                GeoJson geo = RandomDataUtils.randomGeoJson();

                museumsToCreate.add(new MuseumJson(null, title, description, photo, geo));
            }

            List<MuseumJson> createdMuseums = museumClient.createMuseums(museumsToCreate);
            context.getStore(NAMESPACE).put(context.getUniqueId(), createdMuseums);
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(List.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MuseumJson> resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return (List<MuseumJson>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class);
    }
}
