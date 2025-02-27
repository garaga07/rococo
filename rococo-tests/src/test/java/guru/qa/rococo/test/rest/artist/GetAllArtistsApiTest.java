package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.model.rest.ArtistJson;
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

@Order(1)
@Isolated
@ResourceLock(value = "artists", mode = ResourceAccessMode.READ_WRITE)
@RestTest
@DisplayName("GetAllArtists")
public class GetAllArtistsApiTest {
    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 19)
    @ParameterizedTest
    @CsvSource({
            "0, 18, 18",  // Первая страница, size=18 → должно быть 18 записей
            "1, 18, 1",  // Вторая страница, size=18 → должна быть 1 запись
            "2, 18, 0",  // Третья страница, size=18 → пустой список (нет данных)
    })
    @DisplayName("Проверка пагинации списка художников")
    void shouldReturnCorrectNumberOfArtistsForPagination(int page, int size, int expectedCount) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(page, size, null);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(expectedCount, artistList.size(),
                String.format("Expected %d artists on page %d with size %d, but got %d",
                        expectedCount, page, size, artistList.size()));
    }

    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 4, names = {"Da Vinci", "Rembrandt", "Matisse", "Айвазовский"})
    @ParameterizedTest
    @CsvSource({
            "Da Vinci, Da Vinci",     // Полное совпадение
            "DA VINCI, Da Vinci",     // Поиск без учета регистра
            "Da, Da Vinci",           // Частичное совпадение LIKE 'Da%' (начало строки)
            "inci, Da Vinci",         // Частичное совпадение LIKE '%inci' (конец строки)
            "Vin, Da Vinci",          // Частичное совпадение LIKE '%Vin%' (внутри строки)
            "Айвазовский, Айвазовский" // Поиск по кириллическим символам
    })
    @DisplayName("Фильтрация списка художников по существующему имени")
    void shouldReturnArtistWhenSearchingForExistentName(String searchName, String expectedArtistName) {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 10, searchName);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(1, artistList.size(), "Expected only one matching artist but got " + artistList.size());
        assertEquals(expectedArtistName, artistList.getFirst().name(), "Returned artist name does not match the search query");
    }


    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 3, names = {"Picasso", "Van Gogh", "Monet"})
    @Test
    @DisplayName("Фильтрация списка художников по несуществующему имени")
    void shouldReturnEmptyListWhenSearchingForNonExistentName() {
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 10, "NonExistentArtist");
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<ArtistJson> artistList = response.body().getContent();
        assertEquals(0, artistList.size(), "Expected empty list but got " + artistList.size());
    }

    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка художников")
    @Tags({@Tag("artist")})
    @Artist(count = 10)
    @ParameterizedTest
    @MethodSource("provideEmptyAndWhitespaceStrings")
    @DisplayName("Запрос всех художников при пустом или пробельном значении фильтра")
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