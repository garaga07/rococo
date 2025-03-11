package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static java.util.Objects.requireNonNull;

public class ProfileModal extends BaseModal<ProfileModal> {
    private final SelenideElement logoutButton = $x("//button[text()='Выйти']");
    private final SelenideElement avatar = $x("//figure[@data-testid='avatar']//img");
    private final SelenideElement userName = $x("//h4[@class='text-center']");
    private final SelenideElement firstNameInput = $("[name=firstname]");
    private final SelenideElement surnameInput = $("[name=surname]");
    private final SelenideElement photoInput = $("input[type='file']");

    @Step("Get screenshot of profileImage avatar")
    @Nonnull
    public BufferedImage getAvatarScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(avatar.screenshot()));
    }

    @Step("Logout from profile")
    public void logout() {
        logoutButton.click();
    }

    @Step("Set firstName: {0}")
    public ProfileModal setFirstName(String firstName) {
        firstNameInput.clear();
        firstNameInput.setValue(firstName);
        return this;
    }

    @Step("Set surName: {0}")
    public ProfileModal setSurname(String surname) {
        surnameInput.clear();
        surnameInput.setValue(surname);
        return this;
    }

    @Step("Upload photo from classpath")
    @Nonnull
    public ProfileModal uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Check userName: {0}")
    public void checkUsername(String username) {
        this.userName.shouldHave(text("@" + username));
    }

    @Step("Check firstName: {0}")
    public ProfileModal checkFirstname(String firstName) {
        firstNameInput.shouldHave(value(firstName));
        return this;
    }

    @Step("Check surName: {0}")
    public ProfileModal checkSurname(String surname) {
        surnameInput.shouldHave(value(surname));
        return this;
    }

    @Step("Check photo exist")
    @Nonnull
    public ProfileModal checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Check all profile validation errors")
    public void checkAllProfileErrors(String expectedFirstnameError, String expectedSurnameError) {
        checkValidationErrors(new Object[][]{
                {firstNameInput, expectedFirstnameError},
                {surnameInput, expectedSurnameError}
        });
    }
}