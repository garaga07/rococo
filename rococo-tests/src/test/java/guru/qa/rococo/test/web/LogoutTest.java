//package guru.qa.rococo.test.web;
//
//import com.codeborne.selenide.Selenide;
//import guru.qa.rococo.jupiter.annotation.ApiLogin;
//import guru.qa.rococo.jupiter.annotation.User;
//import guru.qa.rococo.jupiter.annotation.meta.WebTest;
//import guru.qa.rococo.page.MainPage;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Severity;
//import io.qameta.allure.SeverityLevel;
//import io.qameta.allure.Story;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Tags;
//import org.junit.jupiter.api.Test;
//
//@WebTest
//@DisplayName("LogoutWeb")
//public class LogoutTest {
//
//    @User
//    @ApiLogin
//    @Story("Завершение сессии")
//    @Severity(SeverityLevel.BLOCKER)
//    @Feature("Выход пользователя")
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Успешный выход из системы и завершение сессии")
//    void shouldSuccessfullyLogoutAndCompleteSession() {
//        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class);
//        mainPage.getHeader()
//                .goToProfileByInitials()
//                .logout();
//        mainPage.checkAlertMessage("Сессия завершена")
//                .getHeader()
//                .checkUserIsNotAuthorized();
//    }
//}