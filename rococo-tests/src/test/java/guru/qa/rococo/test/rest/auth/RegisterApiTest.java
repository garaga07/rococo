package guru.qa.rococo.test.rest.auth;

import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.UserExtension;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.impl.AuthApiClient;
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

import static guru.qa.rococo.utils.RandomDataUtils.randomPassword;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
@DisplayName("RegisterApi")
public class RegisterApiTest {

    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Story("Регистрация")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Регистрация пользователя")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Успешная регистрация нового пользователя")
    void shouldSuccessfullyRegisterNewUser() {
        String password = randomPassword();
        Response<Void> response = authApiClient.register(randomUsername(), password, password);
        assertEquals(201, response.code(), "Expected HTTP status 201 but got " + response.code());
    }

    @Story("Регистрация")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Регистрация пользователя")
    @Tags({@Tag("api")})
    @Test
    @DisplayName("API: Ошибка 400 при несовпадающих паролях при регистрации")
    void shouldFailToRegisterUserWhenPasswordsDoNotMatch() {
        Response<Void> response = authApiClient.register(randomUsername(), randomPassword(), randomPassword());
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
    }

    @User
    @Story("Регистрация")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Регистрация пользователя")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Ошибка 400 при регистрации пользователя с уже существующим именем")
    void shouldFailToRegisterUserWithExistingUsername(UserJson user) {
        String password = randomPassword();
        Response<Void> response = authApiClient.register(user.username(), password, password);
        assertEquals(400, response.code(), "Expected HTTP status 400 but got " + response.code());
    }
}