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

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("LoginApi")
public class LoginApiTest {
    @RegisterExtension
    static final UserExtension userExtension = new UserExtension();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Story("Авторизация")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Авторизация пользователя")
    @Tags({@Tag("api"), @Tag("smoke")})
    @Test
    @DisplayName("API: Успешная авторизация существующего пользователя")
    void shouldSuccessfullyAuthenticateRegisteredUser(UserJson user) {
        String token = authApiClient.doLogin(user.username(), user.testData().password());

        assertNotNull(token, "Returned token should not be null");
        assertFalse(token.isBlank(), "Returned token should not be empty");
        assertEquals(3, token.split("\\.").length, "Returned token should be a valid JWT");
    }
}