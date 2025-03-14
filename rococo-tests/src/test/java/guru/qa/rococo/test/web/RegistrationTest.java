package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static guru.qa.rococo.utils.RandomDataUtils.randomPassword;
import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
@DisplayName("RegistrationWeb")
public class RegistrationTest {

    private static final String ERROR_USERNAME_LENGTH = "Allowed username length should be from 3 to 50 characters";
    private static final String ERROR_PASSWORD_LENGTH = "Allowed password length should be from 3 to 12 characters";

    @Story("Регистрация")
    @Feature("Регистрация нового пользователя")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web"), @Tag("smoke")})
    @Test
    @DisplayName("WEB: Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        String newUsername = randomUsername();
        String password = "12345";
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToLogin()
                .doRegister()
                .fillRegisterPage(newUsername, password, password)
                .submitAndProceedToLogin()
                .fillLoginPage(newUsername, password)
                .submit(mainPage)
                .checkThatPageLoaded();
    }

    @User
    @Story("Регистрация")
    @Feature("Регистрация нового пользователя")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web"), @Tag("smoke")})
    @Test
    @DisplayName("WEB: Ошибка при регистрации пользователя с уже существующим именем")
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToLogin()
                .doRegister()
                .fillRegisterPage(user.username(), user.testData().password(), user.testData().password())
                .submitClick()
                .checkError("Username `" + user.username() + "` already exists");
    }

    @Story("Регистрация")
    @Feature("Регистрация нового пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Tags({@Tag("web"), @Tag("smoke")})
    @Test
    @DisplayName("WEB: Ошибка при регистрации пользователя с несовпадающими паролями")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String newUsername = randomUsername();
        String password = "12345";
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToLogin()
                .doRegister()
                .fillRegisterPage(newUsername, password, "bad password submit")
                .submitClick()
                .checkError("Passwords should be equal");
    }

    static Stream<Arguments> invalidUsernameLengthProvider() {
        return Stream.of(
                Arguments.of(randomUsername(2)),
                Arguments.of(randomUsername(51))
        );
    }

    @Story("Регистрация")
    @Feature("Регистрация нового пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @ParameterizedTest
    @MethodSource("invalidUsernameLengthProvider")
    @DisplayName("WEB: Ошибка при регистрации пользователя с некорректной длиной username")
    void shouldFailRegistrationWithInvalidUsernameLength(String invalidUsername) {
        String password = "12345";
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToLogin()
                .doRegister()
                .fillRegisterPage(invalidUsername, password, password)
                .submitClick()
                .checkError(ERROR_USERNAME_LENGTH);
    }

    static Stream<Arguments> invalidPasswordLengthProvider() {
        return Stream.of(
                Arguments.of(randomPassword(1, 2)),
                Arguments.of(randomPassword(13, 14))
        );
    }

    @Story("Регистрация")
    @Feature("Регистрация нового пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @ParameterizedTest
    @MethodSource("invalidPasswordLengthProvider")
    @DisplayName("WEB: Ошибка при регистрации пользователя с некорректной длиной пароля")
    void shouldFailRegistrationWithInvalidPasswordLength(String invalidPassword) {
        String username = randomUsername(10);
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToLogin()
                .doRegister()
                .fillRegisterPage(username, invalidPassword, invalidPassword)
                .submitClick()
                .checkError(ERROR_PASSWORD_LENGTH);
    }
}