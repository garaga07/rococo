package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.impl.MuseumDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MuseumExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Logger LOGGER = Logger.getLogger(MuseumExtension.class.getName());
    private final MuseumClient museumClient = new MuseumDbClient();
    private static final ThreadLocal<List<MuseumJson>> museumStore = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museumAnno -> {
                    List<MuseumJson> museums = new ArrayList<>();
                    String[] titles = museumAnno.titles();

                    for (int i = 0; i < museumAnno.count(); i++) {
                        String title = (i < titles.length && !titles[i].isEmpty()) ? titles[i] : RandomDataUtils.randomMuseumTitle();
                        String description = RandomDataUtils.randomDescription();
                        String photo = RandomDataUtils.randomBase64Image();
                        museums.add(new MuseumJson(null, title, description, photo, RandomDataUtils.randomGeoJson()));
                    }

                    museumStore.set(museumClient.createMuseums(museums));
                    LOGGER.info(() -> "Created museums: " + museumStore.get());
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        museumStore.get().forEach(museum -> {
            try {
                museumClient.deleteMuseumById(museum.id());
                LOGGER.info(() -> "Deleted museum: " + museum.id());
            } catch (Exception e) {
                LOGGER.severe(() -> "Failed to delete museum: " + museum.id() + " due to " + e.getMessage());
            }
        });
        museumStore.remove();
    }

    public static MuseumJson getMuseumForTest() {
        List<MuseumJson> museums = museumStore.get();
        if (museums == null || museums.isEmpty()) {
            throw new IllegalStateException("No museums were created in MuseumExtension");
        }
        return museums.getFirst();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        boolean hasMuseumAnnotation = extensionContext.getTestMethod()
                .map(m -> m.isAnnotationPresent(Museum.class))
                .or(() -> extensionContext.getTestClass().map(c -> c.isAnnotationPresent(Museum.class)))
                .orElse(false);

        Class<?> type = parameterContext.getParameter().getType();
        return hasMuseumAnnotation && (type == MuseumJson.class || type == MuseumJson[].class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<MuseumJson> museums = museumStore.get();
        if (museums == null || museums.isEmpty()) {
            throw new IllegalStateException("No museums were created in MuseumExtension");
        }

        Class<?> type = parameterContext.getParameter().getType();
        return type == MuseumJson[].class ? museums.toArray(new MuseumJson[0]) : museums.getFirst();
    }
}