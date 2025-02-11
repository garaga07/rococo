package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.pageable.RestResponsePage;
import guru.qa.rococo.service.impl.GatewayApiClient;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
public class ArtistApiTest {

    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    @Artist(count = 5)
    @DisplayName("Get all artists with pagination (should return all 5 artists)")
    void checkGetAllArtistsWithPagination(List<ArtistJson> artists) throws Exception {
        int expectedCount = artists.size();
        Response<RestResponsePage<ArtistJson>> response = gatewayApiClient.getAllArtists(0, 9, null);

        Allure.step("Check that the response has status code 200", () ->
                assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code())
        );

        RestResponsePage<ArtistJson> responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        List<ArtistJson> artistList = responseBody.getContent();
        assertFalse(artistList.isEmpty(), "Artist list should not be empty");

        Allure.step("Check that response contains exactly " + expectedCount + " artists", () ->
                assertEquals(expectedCount, artistList.size(), "Expected " + expectedCount + " artists in response")
        );

        Allure.step("Check that all created artists are present in the response", () -> {
            for (ArtistJson createdArtist : artists) {
                assertTrue(
                        artistList.stream().anyMatch(a -> a.name().equals(createdArtist.name())),
                        "Artist with name " + createdArtist.name() + " is missing in the response"
                );
            }
        });
    }
}