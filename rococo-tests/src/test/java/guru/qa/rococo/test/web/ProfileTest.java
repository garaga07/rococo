package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.component.modal.ProfileModal;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.ScreenDiffResult;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.rococo.utils.RandomDataUtils.randomFirstname;
import static guru.qa.rococo.utils.RandomDataUtils.randomLastname;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
@DisplayName("ProfileWeb")
public class ProfileTest {

    @User
    @ApiLogin
    @Test
    @Story("Профиль пользователя")
    @Feature("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @DisplayName("WEB: Проверка отображения имени пользователя в модальном окне профиля")
    void usernameShouldBeVisibleInProfileModal(UserJson user) {
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToProfileByInitials()
                .checkUsername(user.username());
    }

    @User
    @ApiLogin
    @Test
    @Story("Профиль пользователя")
    @Feature("Редактирование профиля")
    @Severity(SeverityLevel.CRITICAL)
    @Tags({@Tag("web")})
    @DisplayName("WEB: Проверка обновления всех полей профиля")
    void shouldUpdateProfileWithAllFieldsSet() {
        String firstName = randomFirstname();
        String surnameName = randomLastname();
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToProfileByInitials()
                .uploadPhotoFromClasspath("img/profile.png")
                .setFirstName(firstName)
                .setSurname(surnameName)
                .successSubmit();
        mainPage.checkAlertMessage("Профиль обновлен")
                .getHeader()
                .goToProfileByAvatar()
                .checkFirstname(firstName)
                .checkSurname(surnameName)
                .checkPhotoExist();
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/profile-expected.png")
    @Test
    @Story("Профиль пользователя")
    @Feature("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @DisplayName("WEB: Проверка изображения профиля после установки аватара")
    void checkProfileImageTest(BufferedImage expectedProfileImage) throws IOException, InterruptedException {
        ProfileModal profileModal = new ProfileModal();
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);

        mainPage.getHeader()
                .goToProfileByInitials()
                .uploadPhotoFromClasspath("img/profile.png")
                .successSubmit();
        mainPage.getHeader()
                .goToProfileByAvatar();
        Thread.sleep(3000);
        assertFalse(new ScreenDiffResult(
                expectedProfileImage,
                profileModal.getAvatarScreenshot()
        ), "Screen comparison failure");
    }

    @User
    @ApiLogin
    @Test
    @Story("Профиль пользователя")
    @Feature("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @DisplayName("WEB: Успешное обновление профиля с максимальной допустимой длиной имени и фамилии")
    void shouldSuccessfullyUpdateProfileWithMaxAllowedNameAndSurnameLength() {
        String maxValidValue = RandomDataUtils.randomUsername(255);
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToProfileByInitials()
                .setFirstName(maxValidValue)
                .setSurname(maxValidValue)
                .successSubmit();
        mainPage.checkAlertMessage("Профиль обновлен");
    }

    @User
    @ApiLogin
    @Test
    @Story("Профиль пользователя")
    @Feature("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @DisplayName("WEB: Отображение ошибки при превышении максимальной длины имени и фамилии")
    void shouldDisplayErrorWhenFirstnameOrSurnameExceedsMaxLength() {
        String exceedingMaxLengthValue = RandomDataUtils.randomUsername(256);
        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
        mainPage.getHeader()
                .goToProfileByInitials()
                .setFirstName(exceedingMaxLengthValue)
                .setSurname(exceedingMaxLengthValue)
                .errorSubmit()
                .checkFirstnameError("Имя не может быть длиннее 255 символов")
                .checkSurnameError("Фамилия не может быть длиннее 255 символов");
    }
}