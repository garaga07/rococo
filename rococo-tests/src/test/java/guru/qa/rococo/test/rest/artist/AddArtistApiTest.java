package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("AddArtist")
public class AddArtistApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    public static final String ERROR_NAME_REQUIRED = "name: Имя обязательно для заполнения";
    public static final String ERROR_NAME_LENGTH = "name: Имя должно содержать от 3 до 255 символов";
    public static final String ERROR_BIOGRAPHY_REQUIRED = "biography: Биография обязательна для заполнения";
    public static final String ERROR_BIOGRAPHY_LENGTH = "biography: Биография должна содержать от 11 до 2000 символов";
    public static final String ERROR_PHOTO_REQUIRED = "photo: Фото обязательно для заполнения";
    public static final String ERROR_PHOTO_FORMAT = "photo: Фото должно начинаться с 'data:image/'";
    public static final String ERROR_PHOTO_SIZE = "photo: Размер фото не должен превышать 1MB";

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Успешное добавление нового художника")
    void shouldSuccessfullyAddArtist(@Token String token) {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
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

    @Test
    @Story("Художники")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Добавление художника с невалидным значением токена")
    void shouldAddArtistWithIncorrectToken() {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image()
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist("token", artist);
        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    static Stream<Arguments> artistRequiredFieldsProvider() {
        return Stream.of(
                Arguments.of(
                        new ArtistJson(
                                null,
                                null,
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_NAME_REQUIRED
                ),
                Arguments.of(
                        new ArtistJson(null,
                                RandomDataUtils.randomArtistName(),
                                null,
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_BIOGRAPHY_REQUIRED
                ),
                Arguments.of(
                        new ArtistJson(null,
                                RandomDataUtils.randomArtistName(),
                                RandomDataUtils.randomBiography(),
                                null
                        ),
                        ERROR_PHOTO_REQUIRED
                )
        );
    }

    @ParameterizedTest
    @MethodSource("artistRequiredFieldsProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка обязательности полей при добавлении художника")
    void shouldRequiredArtistFields(ArtistJson artist, String expectedDetail) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Error detail mismatch");
        assertEquals("/api/artist", error.instance(), "Error instance mismatch");
    }

    static Stream<Arguments> validArtistNameValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(3),
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image())
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(255),
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image())
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validArtistNameValuesProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка валидных значений для поля name при добавлении художника")
    void shouldSuccessForValidArtistNameValues(ArtistJson artist) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
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

    static Stream<Arguments> invalidArtistNameValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(2),
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_NAME_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(256),
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_NAME_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                "",
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_NAME_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                "             ",
                                RandomDataUtils.randomBiography(),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_NAME_LENGTH
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidArtistNameValuesProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка невалидных значений для поля name при добавлении художника")
    void shouldFailForInvalidArtistNameValues(ArtistJson artist, String expectedDetail) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Error detail mismatch");
        assertEquals("/api/artist", error.instance(), "Error instance mismatch");
    }

    static Stream<Arguments> validArtistBiographyValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                RandomDataUtils.randomBiography(11),
                                RandomDataUtils.randomBase64Image()
                        )
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                RandomDataUtils.randomBiography(2000),
                                RandomDataUtils.randomBase64Image()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validArtistBiographyValuesProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка валидных значений для поля biography при добавлении художника")
    void shouldSuccessForValidArtistBiographyValues(ArtistJson artist) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
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

    static Stream<Arguments> invalidArtistBiographyValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                RandomDataUtils.randomBiography(10),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_BIOGRAPHY_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                RandomDataUtils.randomBiography(2001),
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_BIOGRAPHY_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                "",
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_BIOGRAPHY_LENGTH
                ),
                Arguments.of(
                        new ArtistJson(
                                null,
                                RandomDataUtils.randomArtistName(),
                                "             ",
                                RandomDataUtils.randomBase64Image()
                        ),
                        ERROR_BIOGRAPHY_LENGTH
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidArtistBiographyValuesProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка невалидных значений для поля biography при добавлении художника")
    void shouldFailForInvalidArtistBiographyValues(ArtistJson artist, String expectedDetail) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Error detail mismatch");
        assertEquals("/api/artist", error.instance(), "Error instance mismatch");
    }

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Попытка загрузки изображения больше 1MB при добавлении художника")
    void shouldFailWhenAddingArtistWithImageLargerThan1MB(@Token String token) {
        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                largeImage
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(
                ERROR_PHOTO_SIZE,
                error.detail(),
                "Error detail mismatch"
        );
    }

    @ParameterizedTest
    @MethodSource("invalidPhotoValuesProvider")
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Проверка, что поле photo должно начинаться с 'data:image/'")
    void shouldFailForInvalidPhotoValues(String invalidPhoto, String expectedDetail) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                invalidPhoto
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidPhotoValuesProvider() {
        return Stream.of(
                Arguments.of(
                        "",
                        ERROR_PHOTO_FORMAT
                ),
                Arguments.of(
                        "               ",
                        ERROR_PHOTO_FORMAT
                ),
                Arguments.of(
                        "арапапыурпоаыур",
                        ERROR_PHOTO_FORMAT
                ),
                Arguments.of(
                        "http://example.com/image.png",
                        ERROR_PHOTO_FORMAT
                )
        );
    }

    @Test
    @User
    @ApiLogin()
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление художника")
    @Tags({@Tag("artist")})
    @DisplayName("Успешное добавление нового художника")
    void shouldSuccessfullyAddArtistWithPhotoEqual1MB(@Token String token) {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomBiography(),
                RandomDataUtils.randomBase64Image(700 * 1024) // ~1MB
        );
        Response<ArtistJson> response = gatewayApiClient.addArtist(token, artist);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
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
}