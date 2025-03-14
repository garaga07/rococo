//package guru.qa.rococo.test.rest.painting;
//
//import guru.qa.rococo.jupiter.annotation.Painting;
//import guru.qa.rococo.jupiter.annotation.meta.RestTest;
//import guru.qa.rococo.jupiter.extension.PaintingExtension;
//import guru.qa.rococo.model.ErrorJson;
//import guru.qa.rococo.model.rest.PaintingJson;
//import guru.qa.rococo.model.rest.PaintingResponseJson;
//import guru.qa.rococo.service.impl.GatewayApiClient;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Severity;
//import io.qameta.allure.SeverityLevel;
//import io.qameta.allure.Story;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Tags;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import retrofit2.Response;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@RestTest
//@DisplayName("GetPaintingById")
//public class GetPaintingByIdApiTest {
//    @RegisterExtension
//    static final PaintingExtension paintingExtension = new PaintingExtension();
//    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
//
//    @Story("Картины")
//    @Severity(SeverityLevel.BLOCKER)
//    @Feature("Получение картины по ID")
//    @Tags({@Tag("api")})
//    @Painting
//    @Test
//    @DisplayName("API: Успешное получение данных о картине по ID")
//    void shouldReturnPaintingById(PaintingJson painting) {
//        Response<PaintingResponseJson> response = gatewayApiClient.getPaintingById(painting.id().toString());
//        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
//
//        PaintingResponseJson responseBody = response.body();
//        assertNotNull(responseBody, "Response body should not be null");
//
//        assertAll(
//                () -> assertNotNull(responseBody.id(), "Painting ID should not be null"),
//                () -> assertEquals(painting.title(), responseBody.title(),
//                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'", painting.title(), responseBody.title())),
//                () -> assertEquals(painting.description(), responseBody.description(),
//                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'", painting.description(), responseBody.description())),
//                () -> assertEquals(painting.content(), responseBody.content(),
//                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'", painting.content(), responseBody.content()))
//        );
//
//        // Проверяем данные о музее
//        assertNotNull(responseBody.museumJson(), "Museum data should not be null");
//        assertAll(
//                () -> assertEquals(painting.museumId(), responseBody.museumJson().id(),
//                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'", painting.museumId(), responseBody.museumJson().id())),
//                () -> assertNotNull(responseBody.museumJson().title(), "Museum title should not be null"),
//                () -> assertNotNull(responseBody.museumJson().description(), "Museum description should not be null"),
//                () -> assertNotNull(responseBody.museumJson().photo(), "Museum photo should not be null"),
//                () -> assertNotNull(responseBody.museumJson().geo(), "Museum geo should not be null"),
//                () -> assertNotNull(responseBody.museumJson().geo().city(), "Museum city should not be null"),
//                () -> assertNotNull(responseBody.museumJson().geo().country(), "Museum country should not be null"),
//                () -> assertNotNull(responseBody.museumJson().geo().country().id(), "Museum country ID should not be null"),
//                () -> assertNotNull(responseBody.museumJson().geo().country().name(), "Museum country name should not be null")
//        );
//
//        // Проверяем данные о художнике
//        assertNotNull(responseBody.artistJson(), "Artist data should not be null");
//        assertAll(
//                () -> assertEquals(painting.artistId(), responseBody.artistJson().id(),
//                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'", painting.artistId(), responseBody.artistJson().id())),
//                () -> assertNotNull(responseBody.artistJson().name(), "Artist name should not be null"),
//                () -> assertNotNull(responseBody.artistJson().biography(), "Artist biography should not be null"),
//                () -> assertNotNull(responseBody.artistJson().photo(), "Artist photo should not be null")
//        );
//    }
//
//    @Story("Картины")
//    @Severity(SeverityLevel.NORMAL)
//    @Feature("Получение картины по ID")
//    @Tags({@Tag("api")})
//    @Test
//    @DisplayName("API: Ошибка 404 при попытке получить данные о несуществующей картине")
//    void shouldFailWhenPaintingDoesNotExist() {
//        UUID nonExistentPaintingId = UUID.randomUUID();
//        Response<PaintingResponseJson> response = gatewayApiClient.getPaintingById(nonExistentPaintingId.toString());
//        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
//    }
//
//    @Story("Картины")
//    @Severity(SeverityLevel.MINOR)
//    @Feature("Получение картины по ID")
//    @Tags({@Tag("api")})
//    @Test
//    @DisplayName("API: Ошибка 400 при запросе картины с некорректным UUID")
//    void shouldFailWhenPaintingIdIsInvalid() {
//        String invalidUuid = "3ed0e8878627-4074-b323573c3741499d";
//        Response<PaintingResponseJson> response = gatewayApiClient.getPaintingById(invalidUuid);
//        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
//        ErrorJson error = gatewayApiClient.parseError(response);
//        assertNotNull(error, "ErrorJson should not be null");
//        assertEquals("Bad Request", error.title(), "Unexpected error title");
//        assertEquals(400, error.status(), "Unexpected HTTP status in response");
//        assertEquals(
//                "Failed to convert 'id' with value: '" + invalidUuid + "'",
//                error.detail(),
//                "Unexpected error detail"
//        );
//        assertEquals("/api/painting/" + invalidUuid, error.instance(), "Unexpected error instance");
//    }
//
//
//}