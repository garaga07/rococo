package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.page.ArtistInfoPage;
import guru.qa.rococo.page.ArtistListPage;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.rococo.utils.RandomDataUtils.randomArtistName;
import static guru.qa.rococo.utils.RandomDataUtils.randomBiography;

@WebTest
@DisplayName("ArtistWeb")
public class ArtistTest {

    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();

    @User
    @ApiLogin
    @Story("Художники")
    @Feature("Добавление художника")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное добавление нового художника с минимальным значением длины для имени и биографии")
    void shouldSuccessfullyAddArtistWithMinAllowedNameAndBiographyLength() {
        String name = randomArtistName(3);
        String biography = randomBiography(11);

        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        artistListPage
                .checkThatPageLoaded()
                .addNewArtistButtonClick()
                .setArtistName(name)
                .setArtistBiography(biography)
                .setArtistPhoto("img/artist.png")
                .successSubmit();
        artistListPage
                .checkAlertMessage("Добавлен художник: " + name);
    }

    @User
    @ApiLogin
    @Story("Художники")
    @Feature("Добавление художника")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное добавление нового художника с максимальным значением длины для имени и биографии")
    void shouldSuccessfullyAddArtistWithMaxAllowedNameAndBiographyLength() {
        String name = randomArtistName(255);
        String biography = randomBiography(2000);

        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        artistListPage
                .checkThatPageLoaded()
                .addNewArtistButtonClick()
                .setArtistName(name)
                .setArtistBiography(biography)
                .setArtistPhoto("img/artist.png")
                .successSubmit();
        artistListPage
                .checkAlertMessage("Добавлен художник: " + name);
    }

    @User
    @ApiLogin
    @Story("Художники")
    @Feature("Добавление художника")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки при указании имени и биографии короче допустимой длины при добавлении художника")
    void shouldDisplayErrorWhenAddingArtistWithTooShortNameAndBiography() {
        String tooShortName = RandomDataUtils.randomArtistName(2);
        String tooShortBiography = RandomDataUtils.randomBiography(10);

        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        artistListPage
                .checkThatPageLoaded()
                .addNewArtistButtonClick()
                .setArtistName(tooShortName)
                .setArtistPhoto("img/artist.png")
                .setArtistBiography(tooShortBiography)
                .errorSubmit()
                .checkAllArtistErrors(
                        "Имя не может быть короче 3 символов",
                        "Биография не может быть короче 11 символов"
                );
    }

    @User
    @ApiLogin
    @Story("Художники")
    @Feature("Добавление художника")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки превышения допустимой длины имени и биографии при добавлении художника")
    void shouldDisplayErrorWhenAddingArtistWithExceedingNameAndBiographyLength() {
        String exceedingMaxName = RandomDataUtils.randomArtistName(256);
        String exceedingMaxBiography = RandomDataUtils.randomBiography(2001);
        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        artistListPage
                .checkThatPageLoaded()
                .addNewArtistButtonClick()
                .setArtistName(exceedingMaxName)
                .setArtistPhoto("img/artist.png")
                .setArtistBiography(exceedingMaxBiography)
                .errorSubmit()
                .checkAllArtistErrors(
                        "Имя не может быть длиннее 255 символов",
                        "Биография не может быть длиннее 2000 символов"
                );
    }

    @User
    @ApiLogin
    @Artist()
    @Story("Художники")
    @Feature("Обновление данных художника")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное обновление данных художника с минимально допустимыми значениями для имени и биографии")
    void shouldSuccessfullyUpdateArtistWithMinAllowedNameAndBiographyLength(ArtistJson artist) {
        String name = randomArtistName(3);
        String biography = RandomDataUtils.randomBiography(11);

        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artist.id().toString()), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setArtistName(name)
                .setArtistBiography(biography)
                .setArtistPhoto("img/artist.png")
                .successSubmit();
        artistInfoPage
                .checkAlertMessage("Обновлен художник: " + name);
    }

    @User
    @ApiLogin
    @Artist()
    @Story("Художники")
    @Feature("Обновление данных художника")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное обновление данных художника с максимально допустимыми значениями для имени и биографии")
    void shouldSuccessfullyUpdateArtistWithMaxAllowedNameAndBiographyLength(ArtistJson artist) {
        String name = randomArtistName(255);
        String biography = RandomDataUtils.randomBiography(2000);

        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artist.id().toString()), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setArtistName(name)
                .setArtistBiography(biography)
                .setArtistPhoto("img/artist.png")
                .successSubmit();
        artistInfoPage
                .checkAlertMessage("Обновлен художник: " + name);
    }

    @User
    @ApiLogin
    @Artist()
    @Story("Художники")
    @Feature("Обновление данных художника")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки при указании имени и биографии короче допустимой длины при обновлении художника")
    void shouldDisplayErrorWhenUpdatingArtistWithTooShortNameAndBiography(ArtistJson artist) {
        String tooShortName = RandomDataUtils.randomArtistName(2);
        String tooShortBiography = RandomDataUtils.randomBiography(10);

        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artist.id().toString()), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setArtistName(tooShortName)
                .setArtistBiography(tooShortBiography)
                .setArtistPhoto("img/artist.png")
                .errorSubmit()
                .checkAllArtistErrors(
                        "Имя не может быть короче 3 символов",
                        "Биография не может быть короче 11 символов"
                );
    }

    @User
    @ApiLogin
    @Artist()
    @Story("Художники")
    @Feature("Обновление данных художника")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки превышения допустимой длины имени и биографии при обновлении художника")
    void shouldDisplayErrorWhenUpdatingArtistWithExceedingNameAndBiographyLength(ArtistJson artist) {
        String exceedingMaxName = RandomDataUtils.randomArtistName(256);
        String exceedingMaxBiography = RandomDataUtils.randomBiography(2001);

        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artist.id().toString()), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setArtistName(exceedingMaxName)
                .setArtistBiography(exceedingMaxBiography)
                .setArtistPhoto("img/artist.png")
                .errorSubmit()
                .checkAllArtistErrors(
                        "Имя не может быть длиннее 255 символов",
                        "Биография не может быть длиннее 2000 символов"
                );
    }

    @User
    @ApiLogin
    @Story("Художники")
    @Feature("Поиск художников")
    @Severity(SeverityLevel.MINOR)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Проверка пустого списка при отсутствии найденных художников")
    void shouldDisplayEmptyListWhenNoArtistsFound() {
        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        String emptySearchQuery = RandomDataUtils.randomArtistName(30);
        artistListPage
                .checkThatPageLoaded()
                .filterArtistByName(emptySearchQuery)
                .checkEmptySearchResults(
                        "Художники не найдены",
                        "Для указанного вами фильтра мы не смогли найти художников"
                );
    }

    @User
    @ApiLogin
    @Artist(count = 10)
    @Story("Художники")
    @Feature("Поиск художников")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Отображение найденного художника при фильтрации списка")
    void shouldDisplayArtistAfterFilteringByName(ArtistJson[] artists) {
        ArtistJson artist = artists[artists.length - 1];
        ArtistListPage artistListPage = Selenide.open(ArtistListPage.URL, ArtistListPage.class);
        artistListPage
                .checkThatPageLoaded()
                .filterArtistByName(artist.name())
                .verifyArtistPresence(artist.name());
    }
}