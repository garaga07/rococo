package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.BeforeEachDatabasesExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import guru.qa.rococo.service.impl.GatewayApiClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Isolated
@Order(99)
@RestTest
@DisplayName("GetAllArtist")
public class GetAllArtistApiTest {

    @RegisterExtension
    public static final BeforeEachDatabasesExtension beforeEachDatabasesExtension = new BeforeEachDatabasesExtension();
    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();


    @ParameterizedTest
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 10)
    @CsvSource({
            "0, 3, 3",  // Первая страница, size=3 → должно быть 3 записи
            "2, 3, 3",  // Третья страница, size=3 → должно быть 3 записи
            "3, 3, 1",  // Четвертая страница, size=3 → должна быть 1 запись
            "4, 3, 0"   // Пятая страница, size=3 → пустой список (нет данных)
    })
    @DisplayName("Проверка пагинации списка художников")
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldReturnCorrectNumberOfArtistsForPagination(int page, int size, int expectedCount) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(page, size, null);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(expectedCount, artistList.size(),
                String.format("Expected %d artists on page %d with size %d, but got %d",
                        expectedCount, page, size, artistList.size()));
    }

    @ParameterizedTest
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 4, names = {"Picasso", "Van Gogh", "Monet", "Шишкин"})
    @CsvSource({
            "Picasso, Picasso",   // Полное совпадение
            "PICASSO, Picasso",   // Поиск без учета регистра
            "Pic, Picasso",       // Частичное совпадение LIKE 'Pic%' (начало строки)
            "asso, Picasso",      // Частичное совпадение LIKE '%asso' (конец строки)
            "cas, Picasso",       // Частичное совпадение LIKE '%cas%' (внутри строки)
            "Шишкин, Шишкин"      // Поиск по кириллическим символам
    })
    @DisplayName("Фильтрация списка художников по существующему имени")
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldReturnArtistWhenSearchingForExistentName(String searchName, String expectedArtistName) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 10, searchName);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(1, artistList.size(), "Expected only one matching artist but got " + artistList.size());
        assertEquals(expectedArtistName, artistList.getFirst().name(), "Returned artist name does not match the search query");
    }

    @Test
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 3, names = {"Picasso", "Van Gogh", "Monet"})
    @DisplayName("Фильтрация списка художников по несуществующему имени")
    void shouldReturnEmptyListWhenSearchingForNonExistentName() {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 10, "NonExistentArtist");
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(0, artistList.size(), "Expected empty list but got " + artistList.size());
    }

    @ParameterizedTest
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 10)
    @MethodSource("provideEmptyAndWhitespaceStrings")
    @DisplayName("Запрос всех художников при пустом или пробельном значении фильтра")
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldReturnUnfilteredListWhenSearchNameIsEmptyOrWhitespace(String searchName) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 18, searchName);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<ArtistJson> artistList = response.body().getContent();
        assertFalse(artistList.isEmpty(), "Expected non-empty artist list but got empty response");
        assertEquals(10, artistList.size(), "Expected all 10 artists but got " + artistList.size());

        // Проверка каждого элемента списка
        for (ArtistJson artist : artistList) {
            assertNotNull(artist.id(), "Artist ID should not be null");
            assertNotNull(artist.name(), "Artist name should not be null");
            assertNotNull(artist.biography(), "Artist biography should not be null");
            assertNotNull(artist.photo(), "Artist photo should not be null");
        }
    }

    static Stream<String> provideEmptyAndWhitespaceStrings() {
        return Stream.of(
                "", // Пустая строка
                "   "      // Строка из пробелов
        );
    }
}