package guru.qa.rococo.test.rest.museum;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.utils.RandomDataUtils;
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
@DisplayName("UpdateMuseum")
public class UpdateMuseumApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    @RegisterExtension
    static final MuseumExtension museumExtension = new MuseumExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    public static final String ERROR_ID_REQUIRED = "id: ID музея обязателен для заполнения";

    @Test
    @User
    @ApiLogin()
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление музея")
    @Tags({@Tag("museum")})
    @DisplayName("Успешное обновление данных музея")
    void shouldSuccessfullyUpdateMuseum(@Token String token, MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomMuseumDescription(),
                RandomDataUtils.randomBase64Image(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        MuseumJson responseBody = response.body();
        assertAll(
                () -> assertEquals(updatedMuseum.id(), responseBody.id(), "Museum ID mismatch"),
                () -> assertEquals(updatedMuseum.title(), responseBody.title(), "Museum title mismatch"),
                () -> assertEquals(updatedMuseum.description(), responseBody.description(), "Museum description mismatch"),
                () -> assertEquals(updatedMuseum.photo(), responseBody.photo(), "Museum photo mismatch"),
                () -> assertNotNull(responseBody.geo(), "Museum geo should not be null")
        );
    }

    @Test
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление музея")
    @Tags({@Tag("museum")})
    @DisplayName("Обновление музея с невалидным значением токена")
    void shouldFailToUpdateMuseumWithInvalidToken(MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomMuseumDescription(),
                RandomDataUtils.randomBase64Image(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum("invalid_token", updatedMuseum);

        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    @Test
    @User
    @ApiLogin()
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("museum")})
    @DisplayName("Попытка обновить данные несуществующего музея")
    void shouldFailToUpdateNonExistentMuseum(@Token String token) {
        UUID nonExistentMuseumId = UUID.randomUUID();
        MuseumJson nonExistentMuseum = new MuseumJson(
                nonExistentMuseumId,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomMuseumDescription(),
                RandomDataUtils.randomBase64Image(),
                RandomDataUtils.randomGeoJson()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, nonExistentMuseum);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");

        assertAll(
                () -> assertEquals("Not Found", error.type(), "Error type mismatch"),
                () -> assertEquals("Not Found", error.title(), "Error title mismatch"),
                () -> assertEquals("id: Музей не найден с id: " + nonExistentMuseumId, error.detail(), "Error detail mismatch"),
                () -> assertEquals("/api/museum", error.instance(), "Error instance mismatch")
        );
    }

    @Test
    @User
    @ApiLogin()
    @Story("Музеи")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление музея")
    @Tags({@Tag("museum")})
    @DisplayName("Попытка обновить музей с некорректным ID")
    void shouldFailToUpdateMuseumWithInvalidId(@Token String token) {
        MuseumJson invalidMuseum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomMuseumDescription(),
                RandomDataUtils.randomBase64Image(),
                RandomDataUtils.randomGeoJson()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, invalidMuseum);

        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");

        assertAll(
                () -> assertEquals("Bad Request", error.type(), "Error type mismatch"),
                () -> assertEquals("Bad Request", error.title(), "Error title mismatch"),
                () -> assertEquals(ERROR_ID_REQUIRED, error.detail(), "Error detail mismatch"),
                () -> assertEquals("/api/museum", error.instance(), "Error instance mismatch")
        );
    }
}