//package guru.qa.rococo.test.web;
//
//import com.codeborne.selenide.Selenide;
//import guru.qa.rococo.jupiter.annotation.ApiLogin;
//import guru.qa.rococo.jupiter.annotation.Museum;
//import guru.qa.rococo.jupiter.annotation.User;
//import guru.qa.rococo.jupiter.annotation.meta.WebTest;
//import guru.qa.rococo.jupiter.extension.MuseumExtension;
//import guru.qa.rococo.model.rest.MuseumJson;
//import guru.qa.rococo.page.MuseumInfoPage;
//import guru.qa.rococo.page.MuseumListPage;
//import guru.qa.rococo.utils.RandomDataUtils;
//import io.qameta.allure.Feature;
//import io.qameta.allure.Severity;
//import io.qameta.allure.SeverityLevel;
//import io.qameta.allure.Story;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Tags;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//import static guru.qa.rococo.utils.RandomDataUtils.*;
//
//@WebTest
//@DisplayName("MuseumWeb")
//public class MuseumTest {
//
//    @RegisterExtension
//    static final MuseumExtension museumExtension = new MuseumExtension();
//
//    @User
//    @ApiLogin
//    @Story("Музеи")
//    @Feature("Добавление музея")
//    @Severity(SeverityLevel.BLOCKER)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Успешное добавление нового музея с минимально допустимой длиной названия, описания и города")
//    void shouldSuccessfullyAddMuseumWithMinAllowedTitleDescriptionAndCityLength() {
//        String title = randomMuseumTitle(3);
//        String description = randomDescription(11);
//        String city = randomCity(3);
//
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        museumListPage
//                .checkThatPageLoaded()
//                .addNewMuseumButtonClick()
//                .setMuseumTitle(title)
//                .selectRandomCountry()
//                .setMuseumCity(city)
//                .setMuseumPhoto("img/museum.png")
//                .setMuseumDescription(description)
//                .successSubmit();
//        museumListPage
//                .checkAlertMessage("Добавлен музей: " + title);
//    }
//
//    @User
//    @ApiLogin
//    @Story("Музеи")
//    @Feature("Добавление музея")
//    @Severity(SeverityLevel.BLOCKER)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Успешное добавление нового музея с максимально допустимой длиной названия, описания и города")
//    void shouldSuccessfullyAddMuseumWithMaxAllowedTitleDescriptionAndCityLength() {
//        String title = randomMuseumTitle(255);
//        String description = randomDescription(2000);
//        String city = randomCity(255);
//
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        museumListPage
//                .checkThatPageLoaded()
//                .addNewMuseumButtonClick()
//                .setMuseumTitle(title)
//                .selectRandomCountry()
//                .setMuseumCity(city)
//                .setMuseumPhoto("img/museum.png")
//                .setMuseumDescription(description)
//                .successSubmit();
//        museumListPage
//                .checkAlertMessage("Добавлен музей: " + title);
//    }
//
//
//    @User
//    @ApiLogin
//    @Story("Музеи")
//    @Feature("Добавление музея")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Ошибки при указании названия, описания и города короче допустимой длины при добавлении музея")
//    void shouldDisplayErrorWhenAddingMuseumWithTooShortTitleDescriptionAndCity() {
//        String tooShortTitle = randomMuseumTitle(2);
//        String tooShortDescription = randomDescription(10);
//        String tooShortCity = randomCity(2);
//
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        museumListPage
//                .checkThatPageLoaded()
//                .addNewMuseumButtonClick()
//                .setMuseumTitle(tooShortTitle)
//                .selectRandomCountry()
//                .setMuseumCity(tooShortCity)
//                .setMuseumPhoto("img/museum.png")
//                .setMuseumDescription(tooShortDescription)
//                .errorSubmit()
//                .checkAllMuseumErrors(
//                        "Название не может быть короче 3 символов",
//                        "Описание не может быть короче 11 символов",
//                        "Город не может быть короче 3 символов"
//                );
//    }
//
//    @User
//    @ApiLogin
//    @Story("Музеи")
//    @Feature("Добавление музея")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Ошибки превышения допустимой длины названия, описания и города при добавлении музея")
//    void shouldDisplayErrorWhenAddingMuseumWithExceedingTitleDescriptionAndCityLength() {
//        String exceedingMaxTitle = randomMuseumTitle(256);
//        String exceedingMaxDescription = randomDescription(2001);
//        String exceedingMaxCity = randomCity(256);
//
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        museumListPage
//                .checkThatPageLoaded()
//                .addNewMuseumButtonClick()
//                .setMuseumTitle(exceedingMaxTitle)
//                .selectRandomCountry()
//                .setMuseumCity(exceedingMaxCity)
//                .setMuseumPhoto("img/museum.png")
//                .setMuseumDescription(exceedingMaxDescription)
//                .errorSubmit()
//                .checkAllMuseumErrors(
//                        "Название не может быть длиннее 255 символов",
//                        "Описание не может быть длиннее 2000 символов",
//                        "Город не может быть длиннее 255 символов"
//                );
//    }
//
//
//    @User
//    @ApiLogin
//    @Museum()
//    @Story("Музеи")
//    @Feature("Обновление данных музея")
//    @Severity(SeverityLevel.BLOCKER)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Успешное обновление данных музея с минимально допустимыми значениями для названия, описания и города")
//    void shouldSuccessfullyUpdateMuseumWithMinAllowedTitleDescriptionAndCityLength(MuseumJson museum) {
//        String title = randomMuseumTitle(3);
//        String description = randomDescription(11);
//        String city = randomCity(3);
//
//        MuseumInfoPage museumInfoPage = Selenide.open(MuseumInfoPage.getUrl(museum.id().toString()), MuseumInfoPage.class);
//        museumInfoPage
//                .checkThatPageLoaded()
//                .openEditForm()
//                .setMuseumTitle(title)
//                .setMuseumDescription(description)
//                .setMuseumCity(city)
//                .setMuseumPhoto("img/museum.png")
//                .successSubmit();
//        museumInfoPage
//                .checkAlertMessage("Обновлен музей: " + title);
//    }
//
//    @User
//    @ApiLogin
//    @Museum()
//    @Story("Музеи")
//    @Feature("Обновление данных музея")
//    @Severity(SeverityLevel.BLOCKER)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Успешное обновление данных музея с максимально допустимыми значениями для названия, описания и города")
//    void shouldSuccessfullyUpdateMuseumWithMaxAllowedTitleDescriptionAndCityLength(MuseumJson museum) {
//        String title = randomMuseumTitle(255);
//        String description = randomDescription(2000);
//        String city = randomCity(255);
//
//        MuseumInfoPage museumInfoPage = Selenide.open(MuseumInfoPage.getUrl(museum.id().toString()), MuseumInfoPage.class);
//        museumInfoPage
//                .checkThatPageLoaded()
//                .openEditForm()
//                .setMuseumTitle(title)
//                .setMuseumDescription(description)
//                .setMuseumCity(city)
//                .setMuseumPhoto("img/museum.png")
//                .successSubmit();
//        museumInfoPage
//                .checkAlertMessage("Обновлен музей: " + title);
//    }
//
//
//    @User
//    @ApiLogin
//    @Museum()
//    @Story("Музеи")
//    @Feature("Обновление данных музея")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Ошибки при указании названия, описания и города короче допустимой длины при обновлении музея")
//    void shouldDisplayErrorWhenUpdatingMuseumWithTooShortTitleDescriptionAndCity(MuseumJson museum) {
//        String tooShortTitle = randomMuseumTitle(2);
//        String tooShortDescription = randomDescription(10);
//        String tooShortCity = randomCity(2);
//
//        MuseumInfoPage museumInfoPage = Selenide.open(MuseumInfoPage.getUrl(museum.id().toString()), MuseumInfoPage.class);
//        museumInfoPage
//                .checkThatPageLoaded()
//                .openEditForm()
//                .setMuseumTitle(tooShortTitle)
//                .setMuseumDescription(tooShortDescription)
//                .setMuseumCity(tooShortCity)
//                .setMuseumPhoto("img/museum.png")
//                .errorSubmit()
//                .checkAllMuseumErrors(
//                        "Название не может быть короче 3 символов",
//                        "Описание не может быть короче 11 символов",
//                        "Город не может быть короче 3 символов"
//                );
//    }
//
//
//    @User
//    @ApiLogin
//    @Museum()
//    @Story("Музеи")
//    @Feature("Обновление данных музея")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Ошибки превышения допустимой длины названия, описания и города при обновлении музея")
//    void shouldDisplayErrorWhenUpdatingMuseumWithExceedingTitleDescriptionAndCityLength(MuseumJson museum) {
//        String exceedingMaxTitle = randomMuseumTitle(256);
//        String exceedingMaxDescription = randomDescription(2001);
//        String exceedingMaxCity = randomCity(256);
//
//        MuseumInfoPage museumInfoPage = Selenide.open(MuseumInfoPage.getUrl(museum.id().toString()), MuseumInfoPage.class);
//        museumInfoPage
//                .checkThatPageLoaded()
//                .openEditForm()
//                .setMuseumTitle(exceedingMaxTitle)
//                .setMuseumDescription(exceedingMaxDescription)
//                .setMuseumCity(exceedingMaxCity)
//                .setMuseumPhoto("img/museum.png")
//                .errorSubmit()
//                .checkAllMuseumErrors(
//                        "Название не может быть длиннее 255 символов",
//                        "Описание не может быть длиннее 2000 символов",
//                        "Город не может быть длиннее 255 символов"
//                );
//    }
//
//    @User
//    @ApiLogin
//    @Story("Музеи")
//    @Feature("Поиск музеев")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Проверка пустого списка при отсутствии найденных музеев")
//    void shouldDisplayEmptyListWhenNoMuseumsFound() {
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        String emptySearchQuery = RandomDataUtils.randomMuseumTitle(30);
//        museumListPage
//                .checkThatPageLoaded()
//                .filterMuseumByTitle(emptySearchQuery)
//                .checkEmptySearchResults(
//                        "Музеи не найдены",
//                        "Для указанного вами фильтра мы не смогли найти ни одного музея"
//                );
//    }
//
//
//    @User
//    @ApiLogin
//    @Museum(count = 10)
//    @Story("Музеи")
//    @Feature("Поиск музеев")
//    @Severity(SeverityLevel.NORMAL)
//    @Tags({@Tag("web")})
//    @Test
//    @DisplayName("WEB: Отображение найденного музея при фильтрации списка")
//    void shouldDisplayMuseumAfterFilteringByName(MuseumJson[] museums) {
//        MuseumJson museum = museums[museums.length - 1];
//        MuseumListPage museumListPage = Selenide.open(MuseumListPage.URL, MuseumListPage.class);
//        museumListPage
//                .checkThatPageLoaded()
//                .filterMuseumByTitle(museum.title())
//                .verifyMuseumPresence(museum.title());
//    }
//}