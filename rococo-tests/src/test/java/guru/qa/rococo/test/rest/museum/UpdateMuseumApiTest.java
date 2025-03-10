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
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import retrofit2.Response;

import java.util.UUID;
import java.util.stream.Stream;

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

    // MuseumJson
    public static final String ERROR_ID_REQUIRED = "id: ID музея обязателен для заполнения";
    public static final String ERROR_TITLE_REQUIRED = "title: Название обязательно для заполнения";
    public static final String ERROR_TITLE_LENGTH = "title: Название должно содержать от 3 до 255 символов";
    public static final String ERROR_DESCRIPTION_REQUIRED = "description: Описание обязательно для заполнения";
    public static final String ERROR_DESCRIPTION_LENGTH = "description: Описание должно содержать от 11 до 2000 символов";
    public static final String ERROR_PHOTO_REQUIRED = "photo: Фото обязательно для заполнения";
    public static final String ERROR_PHOTO_SIZE = "photo: Размер фото не должен превышать 1MB";
    public static final String ERROR_PHOTO_FORMAT = "photo: Фото должно начинаться с 'data:image/'";
    public static final String ERROR_GEO_REQUIRED = "geo: Геоданные обязательны для заполнения";
    // GeoJson
    public static final String ERROR_CITY_REQUIRED = "city: Город обязателен для заполнения";
    public static final String ERROR_CITY_LENGTH = "city: Город должен содержать от 3 до 255 символов";
    public static final String ERROR_COUNTRY_REQUIRED = "country: Страна обязательна для заполнения";
    // CountryJson
    public static final String ERROR_COUNTRY_ID_REQUIRED = "country.id: ID страны обязателен для заполнения";


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление данных музея")
    void shouldSuccessfullyUpdateMuseum(@Token String token, MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
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
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 401 при обновлении музея с невалидным токеном")
    void shouldFailToUpdateMuseumWithInvalidToken(MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum("invalid_token", updatedMuseum);

        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    @Test
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 404 при обновлении несуществующего музея")
    void shouldFailToUpdateNonExistentMuseum(@Token String token) {
        UUID nonExistentMuseumId = UUID.randomUUID();
        MuseumJson nonExistentMuseum = new MuseumJson(
                nonExistentMuseumId,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
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

    static Stream<Arguments> museumRequiredFieldsForUpdateProvider() {
        return Stream.of(
                Arguments.of("id", ERROR_ID_REQUIRED),
                Arguments.of("title", ERROR_TITLE_REQUIRED),
                Arguments.of("description", ERROR_DESCRIPTION_REQUIRED),
                Arguments.of("photo", ERROR_PHOTO_REQUIRED),
                Arguments.of("geo", ERROR_GEO_REQUIRED),
                Arguments.of("city", ERROR_CITY_REQUIRED),
                Arguments.of("country", ERROR_COUNTRY_REQUIRED),
                Arguments.of("country.id", ERROR_COUNTRY_ID_REQUIRED)
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("museumRequiredFieldsForUpdateProvider")
    @DisplayName("API: Ошибка 400 при отсутствии обязательных полей в запросе на обновление музея")
    void shouldFailToUpdateMuseumWithNullFields(String fieldToNullify, String expectedDetail) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson invalidMuseum = switch (fieldToNullify) {
            case "id" -> new MuseumJson(null, museum.title(), museum.description(), museum.photo(), museum.geo());
            case "title" -> new MuseumJson(museum.id(), null, museum.description(), museum.photo(), museum.geo());
            case "description" -> new MuseumJson(museum.id(), museum.title(), null, museum.photo(), museum.geo());
            case "photo" -> new MuseumJson(museum.id(), museum.title(), museum.description(), null, museum.geo());
            case "geo" -> new MuseumJson(museum.id(), museum.title(), museum.description(), museum.photo(), null);
            case "city" -> new MuseumJson(museum.id(), museum.title(), museum.description(), museum.photo(),
                    new GeoJson(null, museum.geo().country()));
            case "country" -> new MuseumJson(museum.id(), museum.title(), museum.description(), museum.photo(),
                    new GeoJson(museum.geo().city(), null));
            case "country.id" -> new MuseumJson(museum.id(), museum.title(), museum.description(), museum.photo(),
                    new GeoJson(museum.geo().city(), new CountryJson(null, museum.geo().country().name())));
            default -> throw new IllegalArgumentException("Unexpected field: " + fieldToNullify);
        };

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, invalidMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Unexpected error detail");
    }

    static Stream<Arguments> validMuseumTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomMuseumTitle(3)),
                Arguments.of(RandomDataUtils.randomMuseumTitle(255))
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validMuseumTitleValuesProvider")
    @DisplayName("API: Успешное обновление названия музея валидными значениями")
    void shouldSuccessForValidMuseumTitleValues(String validTitle) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                validTitle,
                museum.description(),
                museum.photo(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertEquals(museum.id(), responseBody.id(), "Museum ID should remain unchanged"),
                () -> assertEquals(validTitle, responseBody.title(),
                        String.format("Museum title mismatch! Expected: '%s', Actual: '%s'", validTitle, responseBody.title())),
                () -> assertEquals(museum.description(), responseBody.description(),
                        String.format("Museum description mismatch! Expected: '%s', Actual: '%s'", museum.description(), responseBody.description())),
                () -> assertEquals(museum.photo(), responseBody.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'", museum.photo(), responseBody.photo())),
                () -> assertNotNull(responseBody.geo(), "Museum geo should not be null"),
                () -> assertEquals(museum.geo().city(), responseBody.geo().city(),
                        String.format("Museum city mismatch! Expected: '%s', Actual: '%s'", museum.geo().city(), responseBody.geo().city())),
                () -> assertEquals(museum.geo().country().id(), responseBody.geo().country().id(),
                        String.format("Museum country ID mismatch! Expected: '%s', Actual: '%s'", museum.geo().country().id(), responseBody.geo().country().id()))
        );
    }

    static Stream<Arguments> invalidMuseumTitleValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomMuseumTitle(2)),
                Arguments.of(RandomDataUtils.randomMuseumTitle(256)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidMuseumTitleValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении названия музея невалидными значениями")
    void shouldFailToUpdateMuseumWithInvalidTitle(String invalidTitle) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                invalidTitle,
                museum.description(),
                museum.photo(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_TITLE_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validMuseumDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(11)),
                Arguments.of(RandomDataUtils.randomDescription(2000))
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validMuseumDescriptionValuesProvider")
    @DisplayName("API: Успешное обновление описания музея валидными значениями")
    void shouldSuccessForValidMuseumDescriptionValues(String validDescription) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                validDescription,
                museum.photo(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(museum.id(), responseBody.id(), "Museum ID should remain unchanged"),
                () -> assertEquals(museum.title(), responseBody.title(),
                        String.format("Museum title mismatch! Expected: '%s', Actual: '%s'",
                                museum.title(), responseBody.title())
                ),
                () -> assertEquals(validDescription, responseBody.description(),
                        String.format("Museum description mismatch! Expected: '%s', Actual: '%s'",
                                validDescription, responseBody.description())
                ),
                () -> assertEquals(museum.photo(), responseBody.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(), responseBody.photo())
                ),
                () -> assertNotNull(responseBody.geo(), "Museum geo should not be null"),
                () -> assertEquals(museum.geo().city(), responseBody.geo().city(),
                        String.format("Museum city mismatch! Expected: '%s', Actual: '%s'",
                                museum.geo().city(), responseBody.geo().city())
                ),
                () -> assertEquals(museum.geo().country().id(), responseBody.geo().country().id(),
                        String.format("Museum country ID mismatch! Expected: '%s', Actual: '%s'",
                                museum.geo().country().id(), responseBody.geo().country().id())
                )
        );
    }

    static Stream<Arguments> invalidMuseumDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomDescription(10)),
                Arguments.of(RandomDataUtils.randomDescription(2001)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidMuseumDescriptionValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении описания музея невалидными значениями")
    void shouldFailToUpdateMuseumWithInvalidDescription(String invalidDescription) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                invalidDescription,
                museum.photo(),
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_DESCRIPTION_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validMuseumCityValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomCity(3)),
                Arguments.of(RandomDataUtils.randomCity(255))
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validMuseumCityValuesProvider")
    @DisplayName("API: Успешное обновление города музея валидными значениями")
    void shouldSuccessForValidMuseumCityValues(String validCity) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                museum.photo(),
                new GeoJson(validCity, museum.geo().country())
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");

        assertAll(
                () -> assertEquals(museum.id(), responseBody.id(), "Museum ID should remain unchanged"),
                () -> assertEquals(museum.title(), responseBody.title(),
                        String.format("Museum title mismatch! Expected: '%s', Actual: '%s'",
                                museum.title(), responseBody.title())
                ),
                () -> assertEquals(museum.description(), responseBody.description(),
                        String.format("Museum description mismatch! Expected: '%s', Actual: '%s'",
                                museum.description(), responseBody.description())
                ),
                () -> assertEquals(museum.photo(), responseBody.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(), responseBody.photo())
                ),
                () -> assertNotNull(responseBody.geo(), "Museum geo should not be null"),
                () -> assertEquals(validCity, responseBody.geo().city(),
                        String.format("Museum city mismatch! Expected: '%s', Actual: '%s'",
                                validCity, responseBody.geo().city())
                ),
                () -> assertEquals(museum.geo().country().id(), responseBody.geo().country().id(),
                        String.format("Museum country ID mismatch! Expected: '%s', Actual: '%s'",
                                museum.geo().country().id(), responseBody.geo().country().id())
                )
        );
    }

    static Stream<Arguments> invalidMuseumCityValuesProvider() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomCity(2)),
                Arguments.of(RandomDataUtils.randomCity(256)),
                Arguments.of(""),
                Arguments.of("             ")
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidMuseumCityValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении города музея невалидными значениями")
    void shouldFailToUpdateMuseumWithInvalidCity(String invalidCity) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                museum.photo(),
                new GeoJson(invalidCity, museum.geo().country())
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CITY_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidMuseumPhotoValuesProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("               "),
                Arguments.of("арапапыурпоаыур"),
                Arguments.of("http://example.com/image.png")
        );
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidMuseumPhotoValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении фото музея невалидными значениями")
    void shouldFailToUpdateMuseumWithInvalidPhoto(String invalidPhoto) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String token = "Bearer " + ApiLoginExtension.getToken();

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                invalidPhoto,
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_FORMAT, error.detail(), "Error detail mismatch");
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при загрузке изображения больше 1MB при обновлении музея")
    void shouldFailToUpdateMuseumWithPhotoLargerThan1MB(@Token String token) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                largeImage,
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());

        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_SIZE, error.detail(), "Error detail mismatch");
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 404 при обновлении музея с несуществующим ID страны")
    void shouldFailToUpdateMuseumWithNonExistentCountryId(@Token String token) {
        MuseumJson museum = MuseumExtension.getMuseumForTest();
        UUID nonExistentCountryId = UUID.randomUUID(); // Генерируем несуществующий ID страны

        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                museum.photo(),
                new GeoJson(museum.geo().city(), new CountryJson(nonExistentCountryId, "Неизвестная страна"))
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("country.id: Страна не найдена с id: " + nonExistentCountryId, error.detail(),
                "Error detail mismatch");
    }


    @User
    @ApiLogin
    @Museum
    @Story("Музеи")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Обновление музея")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление музея с фото размером 1MB")
    void shouldSuccessfullyUpdateMuseumWithPhotoEqual1MB(@Token String token, MuseumJson museum) {
        MuseumJson updatedMuseum = new MuseumJson(
                museum.id(),
                museum.title(),
                museum.description(),
                RandomDataUtils.randomBase64Image(700 * 1024), // ~1MB
                museum.geo()
        );

        Response<MuseumJson> response = gatewayApiClient.updateMuseum(token, updatedMuseum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());

        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(responseBody.id(), museum.id(), "Museum ID should remain unchanged"),
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
                () -> assertEquals(responseBody.photo(), updatedMuseum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                updatedMuseum.photo(),
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
}