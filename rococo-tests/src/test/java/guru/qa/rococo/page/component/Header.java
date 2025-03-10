package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.component.modal.ProfileModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class Header extends BaseComponent<Header> {

    private final SelenideElement mainPageLink = self.$("a[href='/']");
    private final SelenideElement loginButton = $x("//button[text()='Войти']");
    private final SelenideElement avatarInitials = $(".avatar-initials");
    private final SelenideElement avatarImage  = $x("//figure[@data-testid='avatar']//img[1]");

    public Header() {
        super($("#shell-header"));
    }

    @Step("Go to main page")
    @Nonnull
    public MainPage goToMainPage() {
        mainPageLink.click();
        return new MainPage();
    }

    @Step("Go to login page")
    @Nonnull
    public LoginPage goToLogin() {
        loginButton.click();
        return new LoginPage();
    }

    @Step("Go to profile page (click initials)")
    @Nonnull
    public ProfileModal goToProfileByInitials() {
        avatarInitials.click();
        return new ProfileModal();
    }

    @Step("Go to profile page (click avatar)")
    @Nonnull
    public ProfileModal goToProfileByAvatar() {
        avatarImage.click();
        return new ProfileModal();
    }

    @Step("Check that user is authorized")
    public void checkUserIsAuthorized() {
        loginButton.shouldNotBe(visible);
        avatarInitials.shouldBe(visible);
    }

    @Step("Check that user is not authorized")
    public void checkUserIsNotAuthorized() {
        loginButton.shouldBe(visible);
        avatarInitials.shouldNotBe(visible);
    }
}