package guru.qa.rococo.test.rest.museum;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
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
@DisplayName("AddMuseum")
public class AddMuseumApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    // MuseumJson
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

    @Test
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление музея")
    @Tags({@Tag("api"), @Tag("smoke")})
    @DisplayName("API: Успешное создание нового музея")
    void shouldSuccessfullyAddMuseum(@Token String token) {
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                RandomDataUtils.randomGeoJson()
        );
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
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
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
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

    @Test
    @Story("Музеи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Добавление музея")
    @Tags({@Tag("api"), @Tag("smoke")})
    @DisplayName("API: Ошибка 401 при добавлении музея с некорректным токеном")
    void shouldAddMuseumWithIncorrectToken() {
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                RandomDataUtils.randomGeoJson()
        );
        Response<MuseumJson> response = gatewayApiClient.addMuseum("invalid_token", museum);
        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    static Stream<Arguments> museumRequiredFieldsProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                null,
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        ),
                        ERROR_TITLE_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                null,
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        ),
                        ERROR_DESCRIPTION_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                null,
                                RandomDataUtils.randomGeoJson()
                        ),
                        ERROR_PHOTO_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                null
                        ),
                        ERROR_GEO_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(null, RandomDataUtils.randomCountryJson())
                        ),
                        ERROR_CITY_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(), null)
                        ),
                        ERROR_COUNTRY_REQUIRED
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(), new CountryJson(null, "Австралия"))
                        ),
                        ERROR_COUNTRY_ID_REQUIRED
                )
        );
    }

    @ParameterizedTest
    @MethodSource("museumRequiredFieldsProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при отсутствии обязательных полей у музея")
    void shouldRequiredMuseumFields(MuseumJson museum, String expectedDetail) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(expectedDetail, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validMuseumTitleValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(3),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(255),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validMuseumTitleValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Успешное создание музея с допустимой длиной названия")
    void shouldSuccessForValidMuseumTitleValues(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
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
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
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

    static Stream<Arguments> invalidMuseumTitleValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(2), // менее 3 символов
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(256), // более 255 символов
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                "", // пустая строка
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                "             ", // строка из пробелов
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMuseumTitleValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при недопустимой длине названия музея")
    void shouldFailToAddMuseumWithInvalidTitle(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_TITLE_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validMuseumDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(11), // Минимальная допустимая длина
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(2000), // Максимальная допустимая длина
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validMuseumDescriptionValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Успешное создание музея с допустимой длиной описания")
    void shouldSuccessForValidMuseumDescriptionValues(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
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
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
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

    static Stream<Arguments> invalidMuseumDescriptionValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(10), // Менее 11 символов
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(2001), // Более 2000 символов
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                "", // Пустая строка
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                "             ", // Строка из пробелов
                                RandomDataUtils.randomBase64Image(),
                                RandomDataUtils.randomGeoJson()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMuseumDescriptionValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при недопустимой длине описания музея")
    void shouldFailToAddMuseumWithInvalidDescription(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_DESCRIPTION_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validMuseumCityValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(3), RandomDataUtils.randomCountryJson()) // Минимальная допустимая длина
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(255), RandomDataUtils.randomCountryJson()) // Максимальная допустимая длина
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validMuseumCityValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Успешное создание музея с допустимой длиной названия города")
    void shouldSuccessForValidMuseumCityValues(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
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
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
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

    static Stream<Arguments> invalidMuseumCityValuesProvider() {
        return Stream.of(
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(2), RandomDataUtils.randomCountryJson()) // Менее 3 символов
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson(RandomDataUtils.randomCity(256), RandomDataUtils.randomCountryJson()) // Более 255 символов
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson("", RandomDataUtils.randomCountryJson()) // Пустая строка
                        )
                ),
                Arguments.of(
                        new MuseumJson(
                                null,
                                RandomDataUtils.randomMuseumTitle(),
                                RandomDataUtils.randomDescription(),
                                RandomDataUtils.randomBase64Image(),
                                new GeoJson("             ", RandomDataUtils.randomCountryJson()) // Строка из пробелов
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMuseumCityValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при недопустимой длине названия города")
    void shouldFailToAddMuseumWithInvalidCity(MuseumJson museum) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_CITY_LENGTH, error.detail(), "Error detail mismatch");
    }

    @Test
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при загрузке изображения больше 1MB при добавлении музея")
    void shouldFailToAddMuseumWithPhotoLargerThan1MB(@Token String token) {
        String largeImage = RandomDataUtils.randomBase64Image(2 * 700 * 1024); // ~2MB
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                largeImage,
                RandomDataUtils.randomGeoJson()
        );
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(
                ERROR_PHOTO_SIZE,
                error.detail(),
                "Error detail mismatch"
        );
    }

    static Stream<Arguments> invalidMuseumPhotoValuesProvider() {
        return Stream.of(
                Arguments.of(
                        ""
                ),
                Arguments.of(
                        "               "
                ),
                Arguments.of(
                        "арапапыурпоаыур"
                ),
                Arguments.of(
                        "http://example.com/image.png"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMuseumPhotoValuesProvider")
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Ошибка 400 при недопустимом формате фото музея (photo должен начинаться с 'data:image/')")
    void shouldFailToAddMuseumWithInvalidPhoto(String invalidPhoto) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                invalidPhoto,
                RandomDataUtils.randomGeoJson()
        );
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_FORMAT, error.detail(), "Error detail mismatch");
    }

    @Test
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api")})
    @DisplayName("API: Успешное создание музея с фото размером 1MB")
    void shouldSuccessfullyAddMuseumWithPhotoEqual1MB(@Token String token) {
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(700 * 1024), // ~1MB
                RandomDataUtils.randomGeoJson()
        );
        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        MuseumJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertNotNull(responseBody.id(), "Museum ID should not be null"),
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
                () -> assertEquals(responseBody.photo(), museum.photo(),
                        String.format("Museum photo mismatch! Expected: '%s', Actual: '%s'",
                                museum.photo(),
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

    @Test
    @User
    @ApiLogin
    @Story("Музеи")
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Добавление музея")
    @Tags({@Tag("api"), @Tag("smoke")})
    @DisplayName("API: Ошибка 404 при добавлении музея с несуществующим ID страны")
    void shouldFailToAddMuseumWithNonExistentCountryId(@Token String token) {
        UUID nonExistentCountryId = UUID.randomUUID(); // Генерируем несуществующий ID страны
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumTitle(),
                RandomDataUtils.randomDescription(),
                RandomDataUtils.randomBase64Image(),
                new GeoJson(RandomDataUtils.randomCity(), new CountryJson(nonExistentCountryId, "Неизвестная страна"))
        );

        Response<MuseumJson> response = gatewayApiClient.addMuseum(token, museum);

        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("country.id: Страна не найдена с id: " + nonExistentCountryId, error.detail(),
                "Error detail mismatch");
    }
}