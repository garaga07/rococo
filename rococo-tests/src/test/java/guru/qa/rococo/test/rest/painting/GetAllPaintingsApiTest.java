package guru.qa.rococo.test.rest.painting;

import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.PaintingExtension;
import guru.qa.rococo.model.rest.PaintingResponseJson;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import guru.qa.rococo.service.impl.GatewayApiClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Order(3)
@Isolated
@ResourceLock(value = "paintings-artists-museums", mode = ResourceAccessMode.READ_WRITE)
@RestTest
@DisplayName("GetAllPaintings")
public class GetAllPaintingsApiTest {

    @RegisterExtension
    static final PaintingExtension paintingExtension = new PaintingExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка картин")
    @Tags({@Tag("painting")})
    @Painting(count = 10)
    @ParameterizedTest
    @CsvSource({
            "0, 9, 9",  // Первая страница, size=9 → должно быть 9 записей
            "1, 9, 1",  // Вторая страница, size=9 → должна быть 1 запись
            "2, 9, 0",  // Третья страница, size=9 → пустой список (нет данных)
    })
    @DisplayName("Проверка пагинации списка картин")
    void shouldReturnCorrectNumberOfPaintingsForPagination(int page, int size, int expectedCount) {
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getAllPaintings(page, size, null);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertEquals(expectedCount, paintingList.size(),
                String.format("Expected %d paintings on page %d with size %d, but got %d",
                        expectedCount, page, size, paintingList.size()));
    }

    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка картин")
    @Tags({@Tag("painting")})
    @Painting(count = 4, titles = {"Mona Lisa", "The Starry Night", "The Scream", "Девушка с жемчужной сережкой"})
    @ParameterizedTest
    @CsvSource({
            "Mona Lisa, Mona Lisa",   // Полное совпадение
            "MONA LISA, Mona Lisa",   // Поиск без учета регистра
            "Mona, Mona Lisa",        // Частичное совпадение LIKE 'Mona%'
            "Lisa, Mona Lisa",        // Частичное совпадение LIKE '%Lisa'
            "Starry, The Starry Night", // Частичное совпадение LIKE '%Starry%'
            "Девушка с жемчужной сережкой, Девушка с жемчужной сережкой"  // Кириллические символы
    })
    @DisplayName("Фильтрация списка картин по существующему названию")
    void shouldReturnPaintingWhenSearchingForExistentTitle(String searchTitle, String expectedPaintingTitle) {
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getAllPaintings(0, 10, searchTitle);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertEquals(1, paintingList.size(), "Expected only one matching painting but got " + paintingList.size());
        assertEquals(expectedPaintingTitle, paintingList.getFirst().title(), "Returned painting title does not match the search query");
    }

    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка картин")
    @Tags({@Tag("painting")})
    @Painting(count = 3, titles = {"The Birth of Venus", "Guernica", "The Last Supper"})
    @Test
    @DisplayName("Фильтрация списка картин по несуществующему названию")
    void shouldReturnEmptyListWhenSearchingForNonExistentTitle() {
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getAllPaintings(0, 10, "NonExistentPainting");

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertEquals(0, paintingList.size(), "Expected empty list but got " + paintingList.size());
    }

    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка картин")
    @Tags({@Tag("painting")})
    @Painting(count = 2)
    @ParameterizedTest
    @MethodSource("provideEmptyAndWhitespaceStrings")
    @DisplayName("Запрос всех картин при пустом или пробельном значении фильтра")
    void shouldReturnUnfilteredListWhenSearchTitleIsEmptyOrWhitespace(String searchTitle) {
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getAllPaintings(0, 9, searchTitle);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertFalse(paintingList.isEmpty(), "Expected non-empty painting list but got empty response");
        assertEquals(2, paintingList.size(), "Expected all 10 paintings but got " + paintingList.size());

        // Проверка каждого элемента списка
        for (PaintingResponseJson painting : paintingList) {
            assertNotNull(painting.id(), "Painting ID should not be null");
            assertNotNull(painting.title(), "Painting title should not be null");
            assertNotNull(painting.description(), "Painting description should not be null");
            assertNotNull(painting.content(), "Painting content should not be null");

            // Проверка связанного музея
            assertNotNull(painting.museumJson(), "Painting museum should not be null");
            assertNotNull(painting.museumJson().id(), "Museum ID should not be null");
            assertNotNull(painting.museumJson().title(), "Museum title should not be null");
            assertNotNull(painting.museumJson().description(), "Museum description should not be null");
            assertNotNull(painting.museumJson().photo(), "Museum photo should not be null");

            // Проверка геолокации музея
            assertNotNull(painting.museumJson().geo(), "Museum geo should not be null");
            assertNotNull(painting.museumJson().geo().city(), "Museum city should not be null");
            assertNotNull(painting.museumJson().geo().country(), "Museum country should not be null");
            assertNotNull(painting.museumJson().geo().country().id(), "Museum country ID should not be null");
            assertNotNull(painting.museumJson().geo().country().name(), "Museum country name should not be null");

            // Проверка связанного художника
            assertNotNull(painting.artistJson(), "Painting artist should not be null");
            assertNotNull(painting.artistJson().id(), "Artist ID should not be null");
            assertNotNull(painting.artistJson().name(), "Artist name should not be null");
            assertNotNull(painting.artistJson().biography(), "Artist biography should not be null");
            assertNotNull(painting.artistJson().photo(), "Artist photo should not be null");
        }
    }

    static Stream<String> provideEmptyAndWhitespaceStrings() {
        return Stream.of(
                "", // Пустая строка
                "   " // Строка из пробелов
        );
    }
}