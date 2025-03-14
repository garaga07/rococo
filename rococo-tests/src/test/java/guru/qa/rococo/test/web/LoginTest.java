package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
@DisplayName("LoginWeb")
public class LoginTest {

    @User
    @Story("Авторизация")
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Авторизация пользователя")
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешная авторизация пользователя")
    void shouldDisplayProfileAvatarAfterSuccessfulLogin(UserJson user) {
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);

        mainPage.getHeader()
                .goToLogin()
                .fillLoginPage(user.username(), user.testData().password())
                .submit(mainPage)
                .getHeader()
                .checkUserIsAuthorized();
    }

    @Story("Авторизация")
    @Severity(SeverityLevel.CRITICAL )
    @Feature("Авторизация пользователя")
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Авторизация пользователя с неверными учетными данными")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);

        mainPage.getHeader()
                .goToLogin()
                .fillLoginPage(randomUsername(), "BAD")
                .submit(new LoginPage())
                .checkError("Неверные учетные данные пользователя");
    }
}