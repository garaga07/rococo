package guru.qa.rococo.test.rest.museum;

import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.impl.GatewayApiClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("GetMuseumById")
public class GetMuseumByIdApiTest {

    @RegisterExtension
    static final MuseumExtension museumExtension = new MuseumExtension();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение музея по ID")
    @Tags({@Tag("museum")})
    @Museum()
    @DisplayName("Успешное получение данных о музее по ID")
    void shouldReturnMuseumById(MuseumJson museum) {
        Response<MuseumJson> response = gatewayApiClient.getMuseumById(museum.id().toString());

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
                () -> assertEquals(responseBody.title(), museum.title(),
                        String.format("Museum title mismatch! Expected: '%s', Actual: '%s'",
                                museum.title(),
                                responseBody.title())
                ),
                () -> assertEquals(responseBody.description(), museum.description(),
                        String.format("Museum description mismatch! Expected: '%s', Actual: '%s'",
                                museum.description(),
                                responseBody.description())
                ),
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
                                responseBody.photo())
                ),
                () -> assertNotNull(responseBody.geo(), "Museum geo should not be null"),
                () -> assertEquals(responseBody.geo().city(), museum.geo().city(),
                        String.format("Museum city mismatch! Expected: '%s', Actual: '%s'",
                                museum.geo().city(),
                                responseBody.geo().city())
                ),
                () -> assertEquals(responseBody.geo().country().id(), museum.geo().country().id(),
                        String.format("Museum country ID mismatch! Expected: '%s', Actual: '%s'",
                                museum.geo().country().id(),
                                responseBody.geo().country().id())
                )
        );
    }

    @Test
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение музея по ID")
    @Tags({@Tag("museum")})
    @DisplayName("Попытка получить данные о несуществующем музее")
    void shouldFailWhenMuseumDoesNotExist() {
        UUID nonExistentMuseumId = UUID.randomUUID();
        Response<MuseumJson> response = gatewayApiClient.getMuseumById(nonExistentMuseumId.toString());

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
    }

    @Test
    @Story("Музеи")
    @Severity(SeverityLevel.MINOR)
    @Feature("Получение музея по ID")
    @Tags({@Tag("museum")})
    @DisplayName("Попытка получить данные о музее с некорректным UUID")
    void shouldFailWhenMuseumIdIsInvalid() {
        String invalidUuid = "3ed0e8878627-4074-b323573c3741499d";
        Response<MuseumJson> response = gatewayApiClient.getMuseumById(invalidUuid);

        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Bad Request", error.title(), "Unexpected error title");
        assertEquals(400, error.status(), "Unexpected HTTP status in response");
        assertEquals(
                "Failed to convert 'id' with value: '" + invalidUuid + "'",
                error.detail(),
                "Unexpected error detail"
        );
        assertEquals("/api/museum/" + invalidUuid, error.instance(), "Unexpected error instance");
    }
}