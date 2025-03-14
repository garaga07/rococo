package guru.qa.rococo.test.rest.painting;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.PaintingExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.ArtistJson;
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
import retrofit2.Response;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Order(4)
@Isolated
@ResourceLock(value = "paintings-artists-museums", mode = ResourceAccessMode.READ_WRITE)
@RestTest
@DisplayName("GetPaintingsByAuthorId")
public class GetPaintingsByAuthorIdApiTest {

    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    @RegisterExtension
    static final PaintingExtension paintingExtension = new PaintingExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка картин по ID художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Painting(count = 2)
    @Test
    @DisplayName("API: Успешное получение списка картин по ID существующего художника")
    void shouldReturnPaintingsByExistsAuthorId() {
        String authorId = PaintingExtension.getPaintingForTest().artistId().toString();
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getPaintingsByAuthorId(authorId, 0, 9);

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

    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение списка картин по ID художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Painting(count = 10)
    @ParameterizedTest
    @CsvSource({
            "0, 9, 9",  // Первая страница, size=9 → должно быть 9 записей
            "1, 9, 1",  // Вторая страница, size=9 → должна быть 1 запись
            "2, 9, 0",  // Третья страница, size=9 → пустой список (нет данных)
    })
    @DisplayName("API: Корректная пагинация списка картин по ID художника")
    void shouldReturnCorrectPaintingsByAuthorIdWithPagination(int page, int size, int expectedCount) {
        String authorId = PaintingExtension.getPaintingForTest().artistId().toString();
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getPaintingsByAuthorId(authorId, page, size);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertEquals(expectedCount, paintingList.size(),
                String.format("Expected %d paintings on page %d with size %d, but got %d",
                        expectedCount, page, size, paintingList.size()));
    }

    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка картин по ID художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 404 при запросе списка картин для несуществующего художника")
    void shouldFailWhenAuthorDoesNotExist() {
        String nonExistentAuthorId = UUID.randomUUID().toString();
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getPaintingsByAuthorId(nonExistentAuthorId, 0, 9);
        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Artist not found with id: " + nonExistentAuthorId, error.detail(),
                "Error detail mismatch");
    }

    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение списка картин по ID художника")
    @Tags({@Tag("api")})
    @Artist
    @Test
    @DisplayName("API: Получение пустого списка картин для нового художника")
    void shouldReturnEmptyListForNewAuthor(ArtistJson artist) {
        String authorId = artist.id().toString();
        Response<RestResponsePage<PaintingResponseJson>> response = gatewayApiClient.getPaintingsByAuthorId(authorId, 0, 9);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        List<PaintingResponseJson> paintingList = response.body().getContent();
        assertTrue(paintingList.isEmpty(), "Expected an empty list of paintings, but got some records");
    }
}