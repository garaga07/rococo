package guru.qa.rococo.test.rest.painting;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.jupiter.extension.PaintingExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.*;
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
@DisplayName("UpdatePainting")
public class UpdatePaintingApiTest {

    public static final String ERROR_ID_REQUIRED = "id: ID картины обязателен для заполнения";
    public static final String ERROR_TITLE_REQUIRED = "title: Название обязательно для заполнения";
    public static final String ERROR_TITLE_LENGTH = "title: Название должно содержать от 3 до 255 символов";
    public static final String ERROR_DESCRIPTION_REQUIRED = "description: Описание обязательно для заполнения";
    public static final String ERROR_DESCRIPTION_LENGTH = "description: Описание должно содержать от 11 до 2000 символов";
    public static final String ERROR_CONTENT_REQUIRED = "content: Фото обязательно для заполнения";
    public static final String ERROR_CONTENT_SIZE = "content: Размер фото не должен превышать 1MB";
    public static final String ERROR_CONTENT_FORMAT = "content: Фото должно начинаться с 'data:image/'";
    public static final String ERROR_ARTIST_REQUIRED = "artist: Художник обязателен для заполнения";
    public static final String ERROR_ARTIST_ID_REQUIRED = "artist.id: ID художника обязателен для заполнения";

    public static final String ERROR_MUSEUM_REQUIRED = "museum: Музей обязателен для заполнения";
    public static final String ERROR_MUSEUM_ID_REQUIRED = "museum.id: ID музея обязателен для заполнения";



    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    @RegisterExtension
    static final PaintingExtension paintingExtension = new PaintingExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();


    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление данных картины")
    void shouldSuccessfullyUpdatePainting(@Token String token, PaintingJson painting) {
        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);

        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");

        PaintingResponseJson responseBody = response.body();
        assertAll(
                () -> assertEquals(updatedPainting.id(), responseBody.id(), "Painting ID mismatch"),
                () -> assertEquals(updatedPainting.title(), responseBody.title(), "Painting title mismatch"),
                () -> assertEquals(updatedPainting.description(), responseBody.description(), "Painting description mismatch"),
                () -> assertEquals(updatedPainting.content(), responseBody.content(), "Painting content mismatch"),
                () -> assertEquals(updatedPainting.artist().id(), responseBody.artistJson().id(), "Artist ID mismatch"),
                () -> assertEquals(updatedPainting.museum().id(), responseBody.museumJson().id(), "Museum ID mismatch")
        );
    }

    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 401 при обновлении картины с невалидным токеном")
    void shouldUpdatePaintingWithIncorrectToken(PaintingJson painting) {
        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting("invalid_token", updatedPainting);
        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    @User
    @ApiLogin
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 404 при обновлении несуществующей картины")
    void shouldFailToUpdateNonExistentPainting(@Token String token) {
        UUID nonExistentPaintingId = UUID.randomUUID(); // Генерируем случайный ID, которого нет в базе
        PaintingRequestJson nonExistentPainting = new PaintingRequestJson(
                nonExistentPaintingId,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(UUID.randomUUID()),
                new MuseumRef(UUID.randomUUID())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, nonExistentPainting);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("id: Картина не найдена с id: " + nonExistentPaintingId, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 404 при обновлении картины с несуществующим ID музея")
    void shouldFailToUpdatePaintingWithNonExistentMuseumId(@Token String token, PaintingJson painting) {
        UUID nonExistentMuseumId = UUID.randomUUID(); // Генерируем ID музея, которого нет в базе

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(nonExistentMuseumId)
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Museum not found with id: " + nonExistentMuseumId, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 404 при обновлении картины с несуществующим ID художника")
    void shouldFailToUpdatePaintingWithNonExistentArtistId(@Token String token, PaintingJson painting) {
        UUID nonExistentArtistId = UUID.randomUUID(); // Генерируем ID художника, которого нет в базе

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(nonExistentArtistId),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Artist not found with id: " + nonExistentArtistId, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> requiredFieldProviderForPainting() {
        return Stream.of(
                Arguments.of("id", ERROR_ID_REQUIRED),
                Arguments.of("title", ERROR_TITLE_REQUIRED),
                Arguments.of("description", ERROR_DESCRIPTION_REQUIRED),
                Arguments.of("content", ERROR_CONTENT_REQUIRED),
                Arguments.of("artist", ERROR_ARTIST_REQUIRED),
                Arguments.of("artist.id", ERROR_ARTIST_ID_REQUIRED),
                Arguments.of("museum", ERROR_MUSEUM_REQUIRED),
                Arguments.of("museum.id", ERROR_MUSEUM_ID_REQUIRED)
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("requiredFieldProviderForPainting")
    @DisplayName("API: Ошибка 400 при обновлении картины без передачи обязательных полей")
    void shouldFailToUpdatePaintingWithNullFields(String fieldToNullify, String expectedDetail) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson invalidPainting = switch (fieldToNullify) {
            case "id" -> new PaintingRequestJson(null, painting.title(), painting.description(), painting.content(),
                    new ArtistRef(painting.artistId()), new MuseumRef(painting.museumId()));
            case "title" -> new PaintingRequestJson(painting.id(), null, painting.description(), painting.content(),
                    new ArtistRef(painting.artistId()), new MuseumRef(painting.museumId()));
            case "description" -> new PaintingRequestJson(painting.id(), painting.title(), null, painting.content(),
                    new ArtistRef(painting.artistId()), new MuseumRef(painting.museumId()));
            case "content" -> new PaintingRequestJson(painting.id(), painting.title(), painting.description(), null,
                    new ArtistRef(painting.artistId()), new MuseumRef(painting.museumId()));
            case "artist" -> new PaintingRequestJson(painting.id(), painting.title(), painting.description(), painting.content(),
                    null, new MuseumRef(painting.museumId()));
            case "artist.id" -> new PaintingRequestJson(painting.id(), painting.title(), painting.description(), painting.content(),
                    new ArtistRef(null), new MuseumRef(painting.museumId()));
            case "museum" -> new PaintingRequestJson(painting.id(), painting.title(), painting.description(), painting.content(),
                    new ArtistRef(painting.artistId()), null);
            case "museum.id" -> new PaintingRequestJson(painting.id(), painting.title(), painting.description(), painting.content(),
                    new ArtistRef(painting.artistId()), new MuseumRef(null));
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldToNullify);
        };

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, invalidPainting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Unexpected error detail");
    }

    static Stream<Arguments> validPaintingTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomPaintingTitle(3)),
                Arguments.of(RandomDataUtils.randomPaintingTitle(255))
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validPaintingTitleValuesProvider")
    @DisplayName("API: Успешное обновление картины с валидными значениями title")
    void shouldSuccessForValidPaintingTitleValues(String validTitle) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                validTitle,
                painting.description(),
                painting.content(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertEquals(painting.id(), responseBody.id(), "Painting ID should remain unchanged"),
                () -> assertEquals(validTitle, responseBody.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'", validTitle, responseBody.title())),
                () -> assertEquals(painting.description(), responseBody.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'", painting.description(), responseBody.description())),
                () -> assertEquals(painting.content(), responseBody.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'", painting.content(), responseBody.content())),
                () -> assertNotNull(responseBody.artistJson(), "Artist data should not be null"),
                () -> assertEquals(painting.artistId(), responseBody.artistJson().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'", painting.artistId(), responseBody.artistJson().id())),
                () -> assertNotNull(responseBody.museumJson(), "Museum data should not be null"),
                () -> assertEquals(painting.museumId(), responseBody.museumJson().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'", painting.museumId(), responseBody.museumJson().id()))
        );
    }

    static Stream<Arguments> invalidPaintingTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomPaintingTitle(2)),
                Arguments.of(RandomDataUtils.randomPaintingTitle(256)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingTitleValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении картины с невалидными значениями title")
    void shouldFailToUpdatePaintingWithInvalidTitle(String invalidTitle) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                invalidTitle,
                painting.description(),
                painting.content(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_TITLE_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validPaintingDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(11)),
                Arguments.of(RandomDataUtils.randomDescription(2000))
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validPaintingDescriptionValuesProvider")
    @DisplayName("API: Успешное обновление картины с валидными значениями description")
    void shouldSuccessForValidPaintingDescriptionValues(String validDescription) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                painting.title(),
                validDescription,
                painting.content(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(painting.id(), responseBody.id(), "Painting ID should remain unchanged"),
                () -> assertEquals(painting.title(), responseBody.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'",
                                painting.title(), responseBody.title())
                ),
                () -> assertEquals(validDescription, responseBody.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'",
                                validDescription, responseBody.description())
                ),
                () -> assertEquals(painting.content(), responseBody.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'",
                                painting.content(), responseBody.content())
                ),
                () -> assertNotNull(responseBody.artistJson(), "Painting artist should not be null"),
                () -> assertEquals(painting.artistId(), responseBody.artistJson().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.artistId(), responseBody.artistJson().id())
                ),
                () -> assertNotNull(responseBody.museumJson(), "Painting museum should not be null"),
                () -> assertEquals(painting.museumId(), responseBody.museumJson().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.museumId(), responseBody.museumJson().id())
                )
        );
    }

    static Stream<Arguments> invalidPaintingDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(10)),
                Arguments.of(RandomDataUtils.randomDescription(2001)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingDescriptionValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении картины с невалидными значениями description")
    void shouldFailToUpdatePaintingWithInvalidDescription(String invalidDescription) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                painting.title(),
                invalidDescription,
                painting.content(),
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_DESCRIPTION_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidPaintingPhotoValuesProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("               "),
                Arguments.of("арапапыурпоаыур"),
                Arguments.of("http://example.com/image.png")
        );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingPhotoValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении картины с невалидными значениями content")
    void shouldFailToUpdatePaintingWithInvalidContent(String invalidPhoto) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                painting.title(),
                painting.description(),
                invalidPhoto,
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CONTENT_FORMAT, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при загрузке изображения больше 1MB при обновлении картины")
    void shouldFailToUpdatePaintingWithPhotoLargerThan1MB(@Token String token) {
        PaintingJson painting = PaintingExtension.getPaintingForTest();
        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB

        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                painting.title(),
                painting.description(),
                largeImage,
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CONTENT_SIZE, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление картины с фото размером 1MB")
    void shouldSuccessfullyUpdatePaintingWithPhotoEqual1MB(@Token String token, PaintingJson painting) {
        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                painting.id(),
                painting.title(),
                painting.description(),
                RandomDataUtils.randomBase64Image(700 * 1024), // ~1MB
                new ArtistRef(painting.artistId()),
                new MuseumRef(painting.museumId())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.updatePainting(token, updatedPainting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(responseBody.id(), painting.id(), "Painting ID should remain unchanged"),
                () -> assertEquals(responseBody.title(), painting.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'",
                                painting.title(),
                                responseBody.title())
                ),
                () -> assertEquals(responseBody.description(), painting.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'",
                                painting.description(),
                                responseBody.description())
                ),
                () -> assertEquals(responseBody.content(), updatedPainting.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'",
                                updatedPainting.content(),
                                responseBody.content())
                ),
                () -> assertEquals(responseBody.artistJson().id(), painting.artistId(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.artistId(),
                                responseBody.artistJson().id())
                ),
                () -> assertEquals(responseBody.museumJson().id(), painting.museumId(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.museumId(),
                                responseBody.museumJson().id())
                )
        );
    }
}