package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.ArtistJson;
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
@DisplayName("UpdateArtist")
public class UpdateArtistApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    public static final String ERROR_ID_REQUIRED = "id: ID художника обязателен для заполнения";

    @Test
    @User
    @ApiLogin()
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Успешное обновление данных художника")
    void shouldSuccessfullyUpdateArtist(@Token String token, ArtistJson artist) {
        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        ArtistJson responseBody = response.body();
        assertAll(
                () -> assertEquals(updatedArtist.id(), responseBody.id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'", updatedArtist.id(), responseBody.id())),
                () -> assertEquals(updatedArtist.name(), responseBody.name(),
                        String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", updatedArtist.name(), responseBody.name())),
                () -> assertEquals(updatedArtist.biography(), responseBody.biography(),
                        String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", updatedArtist.biography(), responseBody.biography())),
                () -> assertEquals(updatedArtist.photo(), responseBody.photo(),
                        String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", updatedArtist.photo(), responseBody.photo()))
        );
    }

    @Test
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Обновление художника с невалидным значением токена")
    void shouldUpdateArtistWithIncorrectToken(ArtistJson artist) {
        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );
        Response<ArtistJson> response = gatewayApiClient.updateArtist("invalid_token", updatedArtist);
        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Попытка обновить данные несуществующего художника")
    void shouldFailToUpdateNonExistentArtist(@Token String token) {
        UUID nonExistentArtistId = UUID.randomUUID(); // Генерируем случайный ID, которого нет в базе
        ArtistJson nonExistentArtist = new ArtistJson(
                nonExistentArtistId,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, nonExistentArtist);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");

        assertAll(
                () -> assertEquals("Not Found", error.type(), "Error type mismatch"),
                () -> assertEquals("Not Found", error.title(), "Error title mismatch"),
                () -> assertEquals("id: Художник не найден с id: " + nonExistentArtistId, error.detail(), "Error detail mismatch"),
                () -> assertEquals("/api/artist", error.instance(), "Error instance mismatch")
        );
    }

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Попытка обновить художника без ID")
    void shouldFailToUpdateArtistWithoutId(@Token String token) {
        ArtistJson invalidArtist = new ArtistJson(
                null, // ID отсутствует
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );
        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, invalidArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertAll(
                () -> assertEquals("Bad Request", error.type(), "Error type mismatch"),
                () -> assertEquals("Bad Request", error.title(), "Unexpected error title"),
                () -> assertEquals(ERROR_ID_REQUIRED, error.detail(), "Unexpected error detail"),
                () -> assertEquals("/api/artist", error.instance(), "Unexpected error instance")
        );
    }
}