package guru.qa.rococo.test.rest.museum;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import guru.qa.rococo.service.impl.GatewayApiClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Order(2)
@Isolated
@ResourceLock(value = "museums", mode = ResourceAccessMode.READ_WRITE)
@RestTest
@DisplayName("GetAllMuseums")
public class GetAllMuseumsApiTest {
    @RegisterExtension
    static final MuseumExtension museumExtension = new MuseumExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();


    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка музеев")
    @Tags({@Tag("museum")})
    @Museum(count = 5)
    @ParameterizedTest
    @CsvSource({
            "0, 4, 4",  // Первая страница, size=4 → должно быть 4 записи
            "1, 4, 1",  // Вторая страница, size=4 → должна быть 1 запись
            "2, 4, 0",  // Третья страница, size=4 → пустой список (нет данных)
    })
    @DisplayName("Проверка пагинации списка музеев")
    void shouldReturnCorrectNumberOfMuseumsForPagination(int page, int size, int expectedCount) {
        Response<RestResponsePage<MuseumJson>> response = gatewayApiClient.getAllMuseums(page, size, null);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<MuseumJson> museumList = response.body().getContent();
        assertEquals(expectedCount, museumList.size(),
                String.format("Expected %d museums on page %d with size %d, but got %d",
                        expectedCount, page, size, museumList.size()));
    }


    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка музеев")
    @Tags({@Tag("museum")})
    @Museum(count = 4, titles = {"Luvr", "Эрмитаж", "Метрополитен", "Третьяковка"})
    @ParameterizedTest
    @CsvSource({
            "Luvr, Luvr",   // Полное совпадение
            "LUVR, Luvr",   // Поиск без учета регистра
            "Lu, Luvr",     // Частичное совпадение LIKE 'Lu%' (начало строки)
            "vr, Luvr",     // Частичное совпадение LIKE '%vr' (конец строки)
            "uv, Luvr",     // Частичное совпадение LIKE '%uv%' (внутри строки)
            "Третьяковка, Третьяковка"  // Поиск по кириллическим символам
    })
    @DisplayName("Фильтрация списка музеев по существующему названию")
    void shouldReturnMuseumWhenSearchingForExistentTitle(String searchTitle, String expectedMuseumTitle) {
        Response<RestResponsePage<MuseumJson>> response = gatewayApiClient.getAllMuseums(0, 10, searchTitle);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<MuseumJson> museumList = response.body().getContent();
        assertEquals(1, museumList.size(), "Expected only one matching museum but got " + museumList.size());
        assertEquals(expectedMuseumTitle, museumList.getFirst().title(), "Returned museum title does not match the search query");
    }


    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка музеев")
    @Tags({@Tag("museum")})
    @Museum(count = 3, titles = {"British Museum", "Guggenheim Museum", "Museo del Prado"})
    @Test
    @DisplayName("Фильтрация списка музеев по несуществующему названию")
    void shouldReturnEmptyListWhenSearchingForNonExistentTitle() {
        Response<RestResponsePage<MuseumJson>> response = gatewayApiClient.getAllMuseums(0, 10, "NonExistentMuseum");
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<MuseumJson> museumList = response.body().getContent();
        assertEquals(0, museumList.size(), "Expected empty list but got " + museumList.size());
    }


    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка музеев")
    @Tags({@Tag("museum")})
    @Museum(count = 4)
    @ParameterizedTest
    @MethodSource("provideEmptyAndWhitespaceStrings")
    @DisplayName("Запрос всех музеев при пустом или пробельном значении фильтра")
    void shouldReturnUnfilteredListWhenSearchTitleIsEmptyOrWhitespace(String searchTitle) {
        Response<RestResponsePage<MuseumJson>> response = gatewayApiClient.getAllMuseums(0, 4, searchTitle);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<MuseumJson> museumList = response.body().getContent();
        assertFalse(museumList.isEmpty(), "Expected non-empty museum list but got empty response");
        assertEquals(4, museumList.size(), "Expected all 4 museums but got " + museumList.size());

        // Проверка каждого элемента списка
        for (MuseumJson museum : museumList) {
            assertNotNull(museum.id(), "Museum ID should not be null");
            assertNotNull(museum.title(), "Museum title should not be null");
            assertNotNull(museum.description(), "Museum description should not be null");
            assertNotNull(museum.photo(), "Museum photo should not be null");
        }
    }

    static Stream<String> provideEmptyAndWhitespaceStrings() {
        return Stream.of(
                "", // Пустая строка
                "   "      // Строка из пробелов
        );
    }
}