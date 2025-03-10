package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.ArtistJson;
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
@DisplayName("GetArtistById")
public class GetArtistByIdApiTest {
    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();


    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Получение художника по ID")
    @Tags({@Tag("api")})
    @Artist
    @Test
    @DisplayName("API: Успешное получение информации о художнике по ID")
    void shouldReturnArtistById(ArtistJson artist) {
        Response<ArtistJson> response = gatewayApiClient.getArtistById(artist.id().toString());
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        ArtistJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Artist ID should not be null"),
                () -> assertEquals(artist.name(), responseBody.name(),
                        String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", artist.name(), responseBody.name())),
                () -> assertEquals(artist.biography(), responseBody.biography(),
                        String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", artist.biography(), responseBody.biography())),
                () -> assertEquals(artist.photo(), responseBody.photo(),
                        String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", artist.photo(), responseBody.photo()))
        );
    }


    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Получение художника по ID")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 404 при запросе данных несуществующего художника")
    void shouldFailWhenArtistDoesNotExist() {
        UUID nonExistentArtistId = UUID.randomUUID();
        Response<ArtistJson> response = gatewayApiClient.getArtistById(nonExistentArtistId.toString());
        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
    }


    @Story("Художники")
    @Severity(SeverityLevel.MINOR)
    @Feature("Получение художника по ID")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при запросе художника с некорректным ID")
    void shouldFailWhenArtistIdIsInvalid() {
        String invalidUuid = "3ed0e8878627-4074-b323573c3741499d";
        Response<ArtistJson> response = gatewayApiClient.getArtistById(invalidUuid);
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
        assertEquals("/api/artist/" + invalidUuid, error.instance(), "Unexpected error instance");
    }
}