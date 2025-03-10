package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement proceedLoginButton = $(".form__submit");
    private final SelenideElement errorContainer = $(".form__error");

    @Step("Fill register page with credentials: username: {0}, password: {1}, submit password: {2}")
    @Nonnull
    public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
        setUsername(login);
        setPassword(password);
        setPasswordSubmit(passwordSubmit);
        return this;
    }

    @Step("Set username: {0}")
    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: {0}")
    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Confirm password: {0}")
    public void setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
    }

    @Step("Submit registration and proceed to login page")
    @Nonnull
    public LoginPage submitAndProceedToLogin() {
        submitButton.click();
        proceedLoginButton.click();
        return new LoginPage();
    }

    @Step("Click submit button on registration form")
    @Nonnull
    public RegisterPage submitClick() {
        submitButton.click();
        return this;
    }

    @Step("Check error message: {error}")
    public void checkError(String error) {
        errorContainer.shouldHave(text(error));
    }

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public RegisterPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        passwordSubmitInput.should(visible);
        return this;
    }
}