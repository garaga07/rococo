package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.UserJson;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTest
@DisplayName("AddArtist")
public class AddArtistApiTest {
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Добавление художника")
    void shouldReturnEmptyListWhenSearchingForNonExistentName(@Token String token) {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        assertNotNull(response.body().id(), "Artist ID should not be null");
        assertEquals(response.body().name(), artist.name(),
                String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", artist.name(), response.body().name()));

        assertEquals(response.body().biography(), artist.biography(),
                String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", artist.biography(), response.body().biography()));

        assertEquals(response.body().photo(), artist.photo(),
                String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", artist.photo(), response.body().photo()));

    }
}