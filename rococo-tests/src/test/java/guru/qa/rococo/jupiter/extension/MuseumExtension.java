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
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {
                    List<MuseumJson> result = new ArrayList<>();
                    String[] titles = museumAnno.titles();

                    for (int i = 0; i < museumAnno.count(); i++) {
                        String title = (i < titles.length && !titles[i].isEmpty()) ? titles[i] : RandomDataUtils.randomMuseumTitle();
                        String description = RandomDataUtils.randomMuseumDescription();
                        String photo = RandomDataUtils.randomBase64Image();
                        GeoJson geo = RandomDataUtils.randomGeoJson();

                        result.add(new MuseumJson(null, title, description, photo, geo));
                    }

                    List<MuseumJson> createdMuseums = museumClient.createMuseums(result);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdMuseums);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        boolean hasMuseumAnnotation = extensionContext.getTestMethod()
                .map(m -> m.isAnnotationPresent(Museum.class))
                .or(() -> extensionContext.getTestClass().map(c -> c.isAnnotationPresent(Museum.class)))
                .orElse(false);

        Class<?> type = parameterContext.getParameter().getType();
        return hasMuseumAnnotation && (type.isAssignableFrom(MuseumJson.class) || type.isAssignableFrom(MuseumJson[].class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<MuseumJson> museums = (List<MuseumJson>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class);

        if (museums == null || museums.isEmpty()) {
            throw new IllegalStateException("No museums were created in MuseumExtension");
        }

        Class<?> type = parameterContext.getParameter().getType();
        return type.isAssignableFrom(MuseumJson[].class) ? museums.toArray(new MuseumJson[0]) : museums.getFirst();
    }
}