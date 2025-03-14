package guru.qa.rococo.test.rest.userdata;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import guru.qa.rococo.model.ErrorJson;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.UsersClient;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.service.impl.UsersDbClient;
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

import static guru.qa.rococo.utils.RandomDataUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("UpdateUserApi")
public class UpdateUserApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    public static final String ERROR_USER_NAME_LENGTH = "username: Имя пользователя должно содержать от 3 до 30 символов";
    public static final String ERROR_FIRST_NAME_LENGTH = "firstname: Имя не может быть длиннее 255 символов";
    public static final String ERROR_LAST_NAME_LENGTH = "lastname: Фамилия не может быть длиннее 255 символов";
    public static final String ERROR_PHOTO_SIZE = "avatar: Размер аватара не должен превышать 1MB";
    public static final String ERROR_PHOTO_FORMAT = "photo: Фото должно начинаться с 'data:image/'";
    public static final String ERROR_USER_NAME_REQUIRED = "username: Имя пользователя обязательно для заполнения";

    static Stream<Arguments> validUserNameValuesProvider() {
        return Stream.of(
                Arguments.of(randomUsername(3)),
                Arguments.of(randomUsername(30))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api"), @Tag("smoke")})
    @ParameterizedTest
    @MethodSource("validUserNameValuesProvider")
    @DisplayName("API: Успешное обновление информации текущего пользователя с валидным userName")
    void shouldSuccessfullyUpdateUserInfoWithValidUserName(String validUserName) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        UserJson user = UserExtension.getUserJson();
        // Обновляем объект updateUser, добавляя ID текущего пользователя
        UserJson updatedUser = new UserJson(
                user.id(),
                validUserName,
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        UserJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(user.id(), responseBody.id(),
                        String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                () -> assertEquals(updatedUser.username(), responseBody.username(),
                        String.format("User name mismatch! Expected: '%s', Actual: '%s'", updatedUser.username(), responseBody.username())),
                () -> assertEquals(updatedUser.firstname(), responseBody.firstname(),
                        String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", updatedUser.firstname(), responseBody.firstname())),
                () -> assertEquals(updatedUser.lastname(), responseBody.lastname(),
                        String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", updatedUser.lastname(), responseBody.lastname())),
                () -> assertEquals(updatedUser.avatar(), responseBody.avatar(),
                        String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", updatedUser.avatar(), responseBody.avatar()))
        );
    }

    static Stream<Arguments> invalidUserNameValuesProvider() {
        return Stream.of(
                Arguments.of(randomUsername(2)),
                Arguments.of(randomUsername(31))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidUserNameValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении информации текущего пользователя с невалидным userName")
    void shouldFailToUpdateUserInfoWithInvalidUserName(String invalidUserName) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        UserJson user = UserExtension.getUserJson();

        UserJson updatedUser = new UserJson(
                user.id(),
                invalidUserName,
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_USER_NAME_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validUserFirstNameValuesProvider() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(randomUsername(255))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validUserFirstNameValuesProvider")
    @DisplayName("API: Успешное обновление информации текущего пользователя с валидным firstName")
    void shouldSuccessfullyUpdateUserInfoWithValidFirstName(String validFirstName) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        UserJson user = UserExtension.getUserJson();
        // Обновляем объект updateUser, добавляя ID текущего пользователя
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                validFirstName,
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        UserJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(user.id(), responseBody.id(),
                        String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                () -> assertEquals(updatedUser.username(), responseBody.username(),
                        String.format("User name mismatch! Expected: '%s', Actual: '%s'", updatedUser.username(), responseBody.username())),
                () -> assertEquals(updatedUser.firstname(), responseBody.firstname(),
                        String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", updatedUser.firstname(), responseBody.firstname())),
                () -> assertEquals(updatedUser.lastname(), responseBody.lastname(),
                        String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", updatedUser.lastname(), responseBody.lastname())),
                () -> assertEquals(updatedUser.avatar(), responseBody.avatar(),
                        String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", updatedUser.avatar(), responseBody.avatar()))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при обновлении информации текущего пользователя с невалидным firstName")
    void shouldFailToUpdateUserInfoWithInvalidFirstName(@Token String token, UserJson user) {
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomUsername(256),
                randomLastname(),
                randomBase64Image(),
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_FIRST_NAME_LENGTH, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> validUserLastNameValuesProvider() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(randomUsername(255))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("validUserLastNameValuesProvider")
    @DisplayName("API: Успешное обновление информации текущего пользователя с валидным lastName")
    void shouldSuccessfullyUpdateUserInfoWithValidLastName(String validLastName) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        UserJson user = UserExtension.getUserJson();
        // Обновляем объект updateUser, добавляя ID текущего пользователя
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                validLastName,
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        UserJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(user.id(), responseBody.id(),
                        String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                () -> assertEquals(updatedUser.username(), responseBody.username(),
                        String.format("User name mismatch! Expected: '%s', Actual: '%s'", updatedUser.username(), responseBody.username())),
                () -> assertEquals(updatedUser.firstname(), responseBody.firstname(),
                        String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", updatedUser.firstname(), responseBody.firstname())),
                () -> assertEquals(updatedUser.lastname(), responseBody.lastname(),
                        String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", updatedUser.lastname(), responseBody.lastname())),
                () -> assertEquals(updatedUser.avatar(), responseBody.avatar(),
                        String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", updatedUser.avatar(), responseBody.avatar()))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при обновлении информации текущего пользователя с невалидным lastName")
    void shouldFailToUpdateUserInfoWithInvalidLastName(@Token String token, UserJson user) {
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                randomUsername(256),
                randomBase64Image(),
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_LAST_NAME_LENGTH, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Успешное обновление информации о текущем пользователе с фото размером 1MB")
    void shouldSuccessfullyUpdateUserInfoWithPhotoEqual1MB(@Token String token, UserJson user) {
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                randomLastname(),
                randomBase64Image(700 * 1024), // ~1MB
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(200, response.code(), "Expected HTTP status 200 but got " + response.code());
        assertNotNull(response.body(), "Response body should not be null");
        UserJson responseBody = response.body();
        assertNotNull(responseBody, "Response body should not be null");
        assertAll(
                () -> assertEquals(user.id(), responseBody.id(),
                        String.format("User id mismatch! Expected: '%s', Actual: '%s'", user.id(), responseBody.id())),
                () -> assertEquals(updatedUser.username(), responseBody.username(),
                        String.format("User name mismatch! Expected: '%s', Actual: '%s'", updatedUser.username(), responseBody.username())),
                () -> assertEquals(updatedUser.firstname(), responseBody.firstname(),
                        String.format("User firstname mismatch! Expected: '%s', Actual: '%s'", updatedUser.firstname(), responseBody.firstname())),
                () -> assertEquals(updatedUser.lastname(), responseBody.lastname(),
                        String.format("User lastname mismatch! Expected: '%s', Actual: '%s'", updatedUser.lastname(), responseBody.lastname())),
                () -> assertEquals(updatedUser.avatar(), responseBody.avatar(),
                        String.format("User avatar mismatch! Expected: '%s', Actual: '%s'", updatedUser.avatar(), responseBody.avatar()))
        );
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при обновлении информации текущего пользователя с фото размером больше 1MB")
    void shouldFailToUpdateUserInfoWithPhotoLargerThan1MB(@Token String token, UserJson user) {
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                randomLastname(),
                randomBase64Image(2 * 700 * 1024), // ~2MB,
                null
        );

        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_SIZE, error.detail(), "Error detail mismatch");
    }

    static Stream<Arguments> invalidPhotoValuesProvider() {
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

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @ParameterizedTest
    @MethodSource("invalidPhotoValuesProvider")
    @DisplayName("API: Ошибка 400 при обновлении информации текущего пользователя с некорректным значением photo")
    void shouldFailUpdateUserWithInvalidPhotoValues(String invalidPhoto) {
        String token = "Bearer " + ApiLoginExtension.getToken();
        UserJson user = UserExtension.getUserJson();
        // Обновляем объект updateUser, добавляя ID текущего пользователя
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                randomLastname(),
                invalidPhoto,
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, updatedUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_PHOTO_FORMAT, error.detail(), "Error detail mismatch");
    }

    @User
    @Story("Пользователи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 401 при обновлении информации текущего пользователя с невалидным значением токена")
    void shouldUpdateUserInfoWithIncorrectToken(UserJson user) {
        UserJson updatedUser = new UserJson(
                user.id(),
                randomUsername(),
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser("invalid_token", updatedUser);
        assertEquals(401, response.code(), "Expected HTTP status 401 but got " + response.code());
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 404 при попытке обновить данные несуществующего пользователя")
    void shouldUpdateNonExistentUser(@Token String token) {
        UUID nonExistentUserId = UUID.randomUUID(); // Генерируем случайный ID, которого нет в базе
        UserJson nonExistentUser = new UserJson(
                nonExistentUserId,
                randomUsername(),
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, nonExistentUser);
        assertEquals(404, response.code(), "Expected HTTP status 404 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("id: Пользователь не найден по id: " + nonExistentUserId, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при обновлении информации пользователя без указания username")
    void shouldUpdateUserInfoWithoutUserName(@Token String token, UserJson user) {
        UserJson nonExistentUser = new UserJson(
                user.id(),
                null,
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, nonExistentUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals(ERROR_USER_NAME_REQUIRED, error.detail(), "Error detail mismatch");
    }

    @User
    @ApiLogin
    @Story("Пользователи")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Обновление информации о текущем пользователе")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 400 при обновлении информации пользователя с уже существующим username")
    void shouldUpdateUserInfoWithAlreadyExistUserName(@Token String token, UserJson user) {
        UsersClient usersClient = new UsersDbClient();
        UserJson existUser = usersClient.createUser(randomUsername(), randomPassword());
        UserJson nonExistentUser = new UserJson(
                user.id(),
                existUser.username(),
                randomFirstname(),
                randomLastname(),
                randomBase64Image(),
                null
        );
        Response<UserJson> response = gatewayApiClient.updateUser(token, nonExistentUser);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
        ErrorJson error = gatewayApiClient.parseError(response);
        assertNotNull(error, "ErrorJson should not be null");
        assertEquals("username: Имя пользователя '" + existUser.username() + "' уже занято.", error.detail(), "Error detail mismatch");
    }
}