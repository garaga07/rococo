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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.UUID;
import java.util.stream.Stream;

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
    public static final String ERROR_NAME_REQUIRED = "name: Имя обязательно для заполнения";
    public static final String ERROR_NAME_LENGTH = "name: Имя должно содержать от 3 до 255 символов";
    public static final String ERROR_BIOGRAPHY_REQUIRED = "biography: Биография обязательна для заполнения";
    public static final String ERROR_BIOGRAPHY_LENGTH = "biography: Биография должна содержать от 11 до 2000 символов";
    public static final String ERROR_PHOTO_REQUIRED = "photo: Фото обязательно для заполнения";
    public static final String ERROR_PHOTO_FORMAT = "photo: Фото должно начинаться с 'data:image/'";
    public static final String ERROR_PHOTO_SIZE = "photo: Размер фото не должен превышать 1MB";


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Успешное обновление информации о художнике")
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


    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 401 при обновлении художника с некорректным токеном")
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


    @User
    @ApiLogin
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 404 при обновлении несуществующего художника")
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
        assertEquals("id: Художник не найден с id: " + nonExistentArtistId, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> requiredFieldProvider() {
        return Stream.of(
                Arguments.of("id", ERROR_ID_REQUIRED),
                Arguments.of("name", ERROR_NAME_REQUIRED),
                Arguments.of("biography", ERROR_BIOGRAPHY_REQUIRED),
                Arguments.of("photo", ERROR_PHOTO_REQUIRED)
        );
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("requiredFieldProvider")
    @DisplayName("API: Ошибка 400 при отсутствии обязательных полей у художника при обновлении")
    void shouldFailToUpdateArtistWithNullFields(String fieldToNullify, String expectedDetail) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();
        ArtistJson invalidArtist = switch (fieldToNullify) {
            case "id" -> new ArtistJson(null, artist.name(), artist.biography(), artist.photo());
            case "name" -> new ArtistJson(artist.id(), null, artist.biography(), artist.photo());
            case "biography" -> new ArtistJson(artist.id(), artist.name(), null, artist.photo());
            case "photo" -> new ArtistJson(artist.id(), artist.name(), artist.biography(), null);
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldToNullify);
        };

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, invalidArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Unexpected error detail");
    }

    static Stream<Arguments> validArtistNameValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomArtistName(3)),
                Arguments.of(RandomDataUtils.randomArtistName(255))
        );
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validArtistNameValuesProvider")
    @DisplayName("API: Успешное обновление имени художника с допустимой длиной")
    void shouldSuccessForValidArtistNameValues(String validName) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                validName,
                artist.biography(),
                artist.photo()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        ArtistJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(artist.id(), responseBody.id(), "Artist ID should remain unchanged"),
                () -> assertEquals(validName, responseBody.name(),
                        String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", validName, responseBody.name())),
                () -> assertEquals(artist.biography(), responseBody.biography(),
                        String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", artist.biography(), responseBody.biography())),
                () -> assertEquals(artist.photo(), responseBody.photo(),
                        String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", artist.photo(), responseBody.photo()))
        );
    }

    static Stream<Arguments> invalidArtistNameValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomArtistName(2)),
                Arguments.of(RandomDataUtils.randomArtistName(256)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidArtistNameValuesProvider")
    @DisplayName("API: Ошибка 400 при недопустимой длине имени художника при обновлении")
    void shouldFailToUpdateArtistWithInvalidName(String invalidName) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                invalidName,
                artist.biography(),
                artist.photo()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_NAME_LENGTH, error.detail(), "Unexpected error detail");
    }

    static Stream<Arguments> validArtistBiographyValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomBiography(11)),
                Arguments.of(RandomDataUtils.randomBiography(2000))
        );
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validArtistBiographyValuesProvider")
    @DisplayName("API: Успешное обновление биографии художника с допустимой длиной")
    void shouldSuccessForValidArtistBiographyValues(String validBiography) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                artist.name(),
                validBiography,
                artist.photo()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        ArtistJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(artist.id(), responseBody.id(), "Artist ID should remain unchanged"),
                () -> assertEquals(artist.name(), responseBody.name(),
                        String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", artist.name(), responseBody.name())),
                () -> assertEquals(validBiography, responseBody.biography(),
                        String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", validBiography, responseBody.biography())),
                () -> assertEquals(artist.photo(), responseBody.photo(),
                        String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", artist.photo(), responseBody.photo()))
        );
    }

    static Stream<Arguments> invalidArtistBiographyValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomBiography(10)),
                Arguments.of(RandomDataUtils.randomBiography(2001)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidArtistBiographyValuesProvider")
    @DisplayName("API: Ошибка 400 при недопустимой длине биографии художника при обновлении")
    void shouldFailToUpdateArtistWithInvalidBiography(String invalidBiography) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                artist.name(),
                invalidBiography,
                artist.photo()
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_BIOGRAPHY_LENGTH, error.detail(), "Unexpected error detail");
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при загрузке изображения более 1MB при обновлении художника")
    void shouldFailWhenUpdatingArtistWithImageLargerThan1MB(@Token String token) {
        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB
        ArtistJson artist = ArtistExtension.getArtistForTest();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                artist.name(),
                artist.biography(),
                largeImage
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_SIZE, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidPhotoValuesProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("               "),
                Arguments.of("арапапыурпоаыур"),
                Arguments.of("http://example.com/image.png")
        );
    }

    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPhotoValuesProvider")
    @DisplayName("API: Ошибка 400 при недопустимом формате фото художника при обновлении")
    void shouldFailToUpdateArtistWithInvalidPhoto(String invalidPhoto) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        ArtistJson artist = ArtistExtension.getArtistForTest();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                artist.name(),
                artist.biography(),
                invalidPhoto
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_FORMAT, error.detail(), "Error detail mismatch");
    }


    @User
    @ApiLogin
    @Artist
    @Story("Художники")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление художника")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление художника с изображением размером 1MB")
    void shouldSuccessfullyUpdateArtistWithPhotoEqual1MB(@Token String token) {
        ArtistJson artist = ArtistExtension.getArtistForTest();

        ArtistJson updatedArtist = new ArtistJson(
                artist.id(),
                artist.name(),
                artist.biography(),
                RandomDataUtils.randomBase64Image(700 * 1024) // ~1MB
        );

        Response<ArtistJson> response = gatewayApiClient.updateArtist(token, updatedArtist);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        ArtistJson responseBody = response.body();
        assertAll(
                () -> assertEquals(artist.id(), responseBody.id(), "Artist ID should remain unchanged"),
                () -> assertEquals(artist.name(), responseBody.name(),
                        String.format("Artist name mismatch! Expected: '%s', Actual: '%s'", artist.name(), responseBody.name())),
                () -> assertEquals(artist.biography(), responseBody.biography(),
                        String.format("Artist biography mismatch! Expected: '%s', Actual: '%s'", artist.biography(), responseBody.biography())),
                () -> assertEquals(updatedArtist.photo(), responseBody.photo(),
                        String.format("Artist photo mismatch! Expected: '%s', Actual: '%s'", updatedArtist.photo(), responseBody.photo()))
        );
    }
}