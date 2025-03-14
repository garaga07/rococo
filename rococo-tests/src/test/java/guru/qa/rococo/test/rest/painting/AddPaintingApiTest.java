package guru.qa.rococo.test.rest.painting;

import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.*;
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
@DisplayName("AddPainting")
public class AddPaintingApiTest {

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
    static final ArtistExtension artistExtension = new ArtistExtension();
    @RegisterExtension
    static final MuseumExtension museumExtension = new MuseumExtension();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();


    @Test
    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление картины")
    @Tags({@Tag("api"), @Tag("smoke")})
    @DisplayName("API: Успешное добавление новой картины")
    void shouldSuccessfullyAddPainting(@Token String token, ArtistJson artist, MuseumJson museum) {
        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertNotNull(responseBody.id(), "Painting ID should not be null"),
                () -> assertEquals(responseBody.title(), painting.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'",
                                painting.title(), responseBody.title())),
                () -> assertEquals(responseBody.description(), painting.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'",
                                painting.description(), responseBody.description())),
                () -> assertEquals(responseBody.content(), painting.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'",
                                painting.content(), responseBody.content())),
                () -> assertNotNull(responseBody.artistJson(), "Artist data should not be null"),
                () -> assertEquals(responseBody.artistJson().id(), painting.artist().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.artist().id(), responseBody.artistJson().id())),
                () -> assertNotNull(responseBody.museumJson(), "Museum data should not be null"),
                () -> assertEquals(responseBody.museumJson().id(), painting.museum().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.museum().id(), responseBody.museumJson().id()))
        );
    }

    @Test
    @Story("Картины")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление картины")
    @Tags({@Tag("api"), @Tag("smoke")})
    @DisplayName("API: Ошибка 401 при добавлении картины с невалидным токеном")
    void shouldFailToAddPaintingWithInvalidToken() {
        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(UUID.randomUUID()),
                new MuseumRef(UUID.randomUUID())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting("invalid_token", painting);

        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    static Stream<Arguments> requiredFieldProviderForPainting() {
        return Stream.of(
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
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("requiredFieldProviderForPainting")
    @DisplayName("API: Ошибка 400 при отсутствии обязательных полей в запросе на добавление картины")
    void shouldFailToAddPaintingWithMissingFields(String fieldToNullify, String expectedDetail) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson invalidPainting = switch (fieldToNullify) {
            case "title" -> new PaintingRequestJson(
                    null, null, RandomDataUtils.randomDescription(),
                    RandomDataUtils.randomBase64Image(),
                    new ArtistRef(artist.id()), new MuseumRef(museum.id())
            );
            case "description" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), null,
                    RandomDataUtils.randomBase64Image(),
                    new ArtistRef(artist.id()), new MuseumRef(museum.id())
            );
            case "content" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), RandomDataUtils.randomDescription(),
                    null, new ArtistRef(artist.id()), new MuseumRef(museum.id())
            );
            case "artist" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), RandomDataUtils.randomDescription(),
                    RandomDataUtils.randomBase64Image(), null, new MuseumRef(museum.id())
            );
            case "artist.id" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), RandomDataUtils.randomDescription(),
                    RandomDataUtils.randomBase64Image(), new ArtistRef(null), new MuseumRef(museum.id())
            );
            case "museum" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), RandomDataUtils.randomDescription(),
                    RandomDataUtils.randomBase64Image(), new ArtistRef(artist.id()), null
            );
            case "museum.id" -> new PaintingRequestJson(
                    null, RandomDataUtils.randomPaintingTitle(), RandomDataUtils.randomDescription(),
                    RandomDataUtils.randomBase64Image(), new ArtistRef(artist.id()), new MuseumRef(null)
            );
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldToNullify);
        };

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, invalidPainting);

        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Unexpected error detail");
    }

    static Stream<Arguments> validPaintingTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomPaintingTitle(3)),   // Минимальная допустимая длина
                Arguments.of(RandomDataUtils.randomPaintingTitle(255))  // Максимальная допустимая длина
        );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validPaintingTitleValuesProvider")
    @DisplayName("API: Успешное добавление картины с валидными значениями в поле title")
    void shouldSuccessForValidPaintingTitleValues(String validTitle) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                validTitle,
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertNotNull(responseBody.id(), "Painting ID should not be null"),
                () -> assertEquals(validTitle, responseBody.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'", validTitle, responseBody.title())),
                () -> assertEquals(painting.description(), responseBody.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'", painting.description(), responseBody.description())),
                () -> assertEquals(painting.content(), responseBody.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'", painting.content(), responseBody.content())),
                () -> assertEquals(painting.artist().id(), responseBody.artistJson().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'", painting.artist().id(), responseBody.artistJson().id())),
                () -> assertEquals(painting.museum().id(), responseBody.museumJson().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'", painting.museum().id(), responseBody.museumJson().id()))
        );
    }

    static Stream<Arguments> invalidPaintingTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomPaintingTitle(2)),   // Менее 3 символов
                Arguments.of(RandomDataUtils.randomPaintingTitle(256)), // Более 255 символов
                Arguments.of(""),                                       // Пустая строка
                Arguments.of("             ")                           // Строка из пробелов
        );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingTitleValuesProvider")
    @DisplayName("API: Ошибка 400 при добавлении картины с невалидными значениями в поле title")
    void shouldFailToAddPaintingWithInvalidTitle(String invalidTitle) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                invalidTitle,
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_TITLE_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validPaintingDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(11)),   // Минимальная допустимая длина
                Arguments.of(RandomDataUtils.randomDescription(2000))  // Максимальная допустимая длина
        );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validPaintingDescriptionValuesProvider")
    @DisplayName("API: Успешное добавление картины с валидными значениями в поле description")
    void shouldSuccessForValidPaintingDescriptionValues(String validDescription) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                validDescription,
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertNotNull(responseBody.id(), "Painting ID should not be null"),
                () -> assertEquals(validDescription, responseBody.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'",
                                validDescription, responseBody.description())),
                () -> assertEquals(painting.title(), responseBody.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'",
                                painting.title(), responseBody.title())),
                () -> assertEquals(painting.content(), responseBody.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'",
                                painting.content(), responseBody.content())),
                () -> assertEquals(painting.artist().id(), responseBody.artistJson().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.artist().id(), responseBody.artistJson().id())),
                () -> assertEquals(painting.museum().id(), responseBody.museumJson().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.museum().id(), responseBody.museumJson().id()))
        );
    }

    static Stream<Arguments> invalidPaintingDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(10)),   // Менее 11 символов
                Arguments.of(RandomDataUtils.randomDescription(2001)), // Более 2000 символов
                Arguments.of(""),                                      // Пустая строка
                Arguments.of("             ")                          // Строка из пробелов
        );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingDescriptionValuesProvider")
    @DisplayName("API: Ошибка 400 при добавлении картины с невалидными значениями в поле description")
    void shouldFailToAddPaintingWithInvalidDescription(String invalidDescription) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                invalidDescription,
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_DESCRIPTION_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidPaintingContentValuesProvider() {
        return Stream.of(
                Arguments.of(""),                                      // Пустая строка
                Arguments.of("               "),                      // Строка из пробелов
                Arguments.of("invalidContent12345"),                  // Некорректное содержимое
                Arguments.of("http://example.com/image.png")          // URL вместо Base64
        );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPaintingContentValuesProvider")
    @DisplayName("API: Ошибка 400 при добавлении картины с невалидными значениями в поле content")
    void shouldFailToAddPaintingWithInvalidContent(String invalidContent) {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                invalidContent,
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CONTENT_FORMAT, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при загрузке изображения больше 1MB при добавлении картины")
    void shouldFailToAddPaintingWithPhotoLargerThan1MB() {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                largeImage,
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CONTENT_SIZE, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное добавление картины с фото размером 1MB")
    void shouldSuccessfullyAddPaintingWithPhotoEqual1MB() {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        String validImage = RandomDataUtils.randomBase64Image(700 * 1024); // ~1MB

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                validImage,
                new ArtistRef(artist.id()),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        PaintingResponseJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertNotNull(responseBody.id(), "Painting ID should not be null"),
                () -> assertEquals(painting.title(), responseBody.title(),
                        String.format("Painting title mismatch! Expected: '%s', Actual: '%s'",
                                painting.title(), responseBody.title())),
                () -> assertEquals(painting.description(), responseBody.description(),
                        String.format("Painting description mismatch! Expected: '%s', Actual: '%s'",
                                painting.description(), responseBody.description())),
                () -> assertEquals(validImage, responseBody.content(),
                        String.format("Painting content mismatch! Expected: '%s', Actual: '%s'",
                                validImage, responseBody.content())),
                () -> assertEquals(painting.artist().id(), responseBody.artistJson().id(),
                        String.format("Artist ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.artist().id(), responseBody.artistJson().id())),
                () -> assertEquals(painting.museum().id(), responseBody.museumJson().id(),
                        String.format("Museum ID mismatch! Expected: '%s', Actual: '%s'",
                                painting.museum().id(), responseBody.museumJson().id()))
        );
    }

    @User
    @ApiLogin
    @Museum
    @Story("Картины")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 404 при добавлении картины с несуществующим ID художника")
    void shouldFailToAddPaintingWithNonExistentArtistId() {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        UUID nonExistentArtistId = UUID.randomUUID(); // Генерируем случайный ID, которого нет в базе

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(nonExistentArtistId),
                new MuseumRef(museum.id())
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Artist not found with id: " + nonExistentArtistId, error.detail(),
                "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Artist
    @Story("Картины")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Добавление картины")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 404 при добавлении картины с несуществующим ID музея")
    void shouldFailToAddPaintingWithNonExistentMuseumId() {
        ArtistJson artist = ArtistExtension.getArtistForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        UUID nonExistentMuseumId = UUID.randomUUID(); // Генерируем случайный ID, которого нет в базе

        PaintingRequestJson painting = new PaintingRequestJson(
                null,
                RandomDataUtils.randomPaintingTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new ArtistRef(artist.id()),
                new MuseumRef(nonExistentMuseumId)
        );

        Response<PaintingResponseJson> response = gatewayApiClient.addPainting(token, painting);
        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("Museum not found with id: " + nonExistentMuseumId, error.detail(),
                "Error detail mismatch");
    }
}