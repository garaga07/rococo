package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.jupiter.extension.PaintingExtension;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.ArtistInfoPage;
import guru.qa.rococo.page.PaintingInfoPage;
import guru.qa.rococo.page.PaintingListPage;
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

import static guru.qa.rococo.utils.RandomDataUtils.randomDescription;
import static guru.qa.rococo.utils.RandomDataUtils.randomPaintingTitle;

@WebTest
@DisplayName("PaintingWeb")
public class PaintingTest {

    @RegisterExtension
    static final ArtistExtension artistExtension = new ArtistExtension();
    @RegisterExtension
    static final MuseumExtension museumExtension = new MuseumExtension();
    @RegisterExtension
    static final PaintingExtension paintingExtension = new PaintingExtension();


    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Feature("Добавление картины")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное добавление новой картины с минимально допустимой длиной названия и описания")
    void shouldSuccessfullyAddPaintingWithMinAllowedTitleAndDescriptionLength() {
        String title = randomPaintingTitle(3);
        String description = randomDescription(11);

        PaintingListPage paintingListPage = Selenide.open(PaintingListPage.URL, PaintingListPage.class);
        paintingListPage
                .checkThatPageLoaded()
                .addNewPaintingButtonClick()
                .setPaintingTitle(title)
                .setPaintingPhoto("img/painting.png")
                .selectRandomAuthor()
                .setPaintingDescription(description)
                .selectRandomMuseum()
                .successSubmit();
        paintingListPage
                .checkAlertMessage("Добавлена картина: " + title);
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Feature("Добавление картины")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки при указании названия и описания короче допустимой длины при добавлении картины")
    void shouldDisplayErrorWhenAddingPaintingWithTooShortTitleAndDescription() {
        String tooShortTitle = randomPaintingTitle(2);
        String tooShortDescription = randomDescription(10);

        PaintingListPage paintingListPage = Selenide.open(PaintingListPage.URL, PaintingListPage.class);
        paintingListPage
                .checkThatPageLoaded()
                .addNewPaintingButtonClick()
                .setPaintingTitle(tooShortTitle)
                .setPaintingPhoto("img/painting.png")
                .selectRandomAuthor()
                .setPaintingDescription(tooShortDescription)
                .selectRandomMuseum()
                .errorSubmit()
                .checkAllPaintingErrors(
                        "Название не может быть короче 3 символов",
                        "Описание не может быть короче 11 символов"
                );
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Feature("Добавление картины")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки превышения допустимой длины названия и описания при добавлении картины")
    void shouldDisplayErrorWhenAddingPaintingWithExceedingTitleAndDescriptionLength() {
        String exceedingMaxTitle = randomPaintingTitle(256);
        String exceedingMaxDescription = randomDescription(2001);

        PaintingListPage paintingListPage = Selenide.open(PaintingListPage.URL, PaintingListPage.class);
        paintingListPage
                .checkThatPageLoaded()
                .addNewPaintingButtonClick()
                .setPaintingTitle(exceedingMaxTitle)
                .setPaintingPhoto("img/painting.png")
                .selectRandomAuthor()
                .setPaintingDescription(exceedingMaxDescription)
                .selectRandomMuseum()
                .errorSubmit()
                .checkAllPaintingErrors(
                        "Название не может быть длиннее 255 символов",
                        "Описание не может быть длиннее 2000 символов"
                );

    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Feature("Обновление данных картины")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное обновление данных картины с минимально допустимыми значениями для названия и описания")
    void shouldSuccessfullyUpdatePaintingWithMinAllowedTitleAndDescriptionLength(PaintingJson painting) {
        String title = randomPaintingTitle(3);
        String description = randomDescription(11);

        PaintingInfoPage paintingInfoPage = Selenide.open(PaintingInfoPage.getUrl(painting.id().toString()), PaintingInfoPage.class);
        paintingInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setPaintingTitle(title)
                .setPaintingDescription(description)
                .setPaintingPhoto("img/painting.png")
                .successSubmit();
        paintingInfoPage
                .checkAlertMessage("Обновлена картина: " + title);
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Feature("Обновление данных картины")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное обновление данных картины с максимально допустимыми значениями для названия и описания")
    void shouldSuccessfullyUpdatePaintingWithMaxAllowedTitleAndDescriptionLength(PaintingJson painting) {
        String title = randomPaintingTitle(255);
        String description = randomDescription(2000);

        PaintingInfoPage paintingInfoPage = Selenide.open(PaintingInfoPage.getUrl(painting.id().toString()), PaintingInfoPage.class);
        paintingInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setPaintingTitle(title)
                .setPaintingDescription(description)
                .setPaintingPhoto("img/painting.png")
                .successSubmit();
        paintingInfoPage
                .checkAlertMessage("Обновлена картина: " + title);
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Feature("Обновление данных картины")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки при указании названия и описания короче допустимой длины при обновлении картины")
    void shouldDisplayErrorWhenUpdatingPaintingWithTooShortTitleAndDescription(PaintingJson painting) {
        String tooShortTitle = randomPaintingTitle(2);
        String tooShortDescription = randomDescription(10);

        PaintingInfoPage paintingInfoPage = Selenide.open(PaintingInfoPage.getUrl(painting.id().toString()), PaintingInfoPage.class);
        paintingInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setPaintingTitle(tooShortTitle)
                .setPaintingDescription(tooShortDescription)
                .setPaintingPhoto("img/painting.png")
                .errorSubmit()
                .checkAllPaintingErrors(
                        "Название не может быть короче 3 символов",
                        "Описание не может быть короче 11 символов"
                );
    }

    @User
    @ApiLogin
    @Painting
    @Story("Картины")
    @Feature("Обновление данных картины")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Ошибки превышения допустимой длины названия и описания при обновлении картины")
    void shouldDisplayErrorWhenUpdatingPaintingWithExceedingTitleAndDescriptionLength(PaintingJson painting) {
        String exceedingMaxTitle = randomPaintingTitle(256);
        String exceedingMaxDescription = randomDescription(2001);

        PaintingInfoPage paintingInfoPage = Selenide.open(PaintingInfoPage.getUrl(painting.id().toString()), PaintingInfoPage.class);
        paintingInfoPage
                .checkThatPageLoaded()
                .openEditForm()
                .setPaintingTitle(exceedingMaxTitle)
                .setPaintingDescription(exceedingMaxDescription)
                .setPaintingPhoto("img/painting.png")
                .errorSubmit()
                .checkAllPaintingErrors(
                        "Название не может быть длиннее 255 символов",
                        "Описание не может быть длиннее 2000 символов"
                );
    }

    @User
    @ApiLogin
    @Story("Картины")
    @Feature("Поиск картин")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Проверка пустого списка при отсутствии найденных картин")
    void shouldDisplayEmptyListWhenNoPaintingsFound() {
        PaintingListPage paintingListPage = Selenide.open(PaintingListPage.URL, PaintingListPage.class);
        String emptySearchQuery = RandomDataUtils.randomPaintingTitle(30);
        paintingListPage
                .checkThatPageLoaded()
                .filterPaintingByTitle(emptySearchQuery)
                .checkEmptySearchResults(
                        "Картины не найдены",
                        "Для указанного вами фильтра мы не смогли найти ни одной картины"
                );
    }

    @User
    @ApiLogin
    @Painting(count = 10)
    @Story("Картины")
    @Feature("Поиск картин")
    @Severity(SeverityLevel.NORMAL)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Отображение найденной картины при фильтрации списка")
    void shouldDisplayPaintingAfterFilteringByName(PaintingJson[] paintings) {
        PaintingJson painting = paintings[paintings.length - 1];
        PaintingListPage paintingListPage = Selenide.open(PaintingListPage.URL, PaintingListPage.class);
        paintingListPage
                .checkThatPageLoaded()
                .filterPaintingByTitle(painting.title())
                .verifyPaintingPresence(painting.title());
    }

    @User
    @ApiLogin
    @Artist
    @Museum
    @Story("Картины")
    @Feature("Добавление картины")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Успешное добавление новой картины со страницы художника без выбора автора")
    void shouldAddPaintingFromArtistPageWithoutAuthorSelection(ArtistJson artist) {
        String title = randomPaintingTitle();
        String description = randomDescription();

        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artist.id().toString()), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .addNewPaintingButtonClick()
                .verifyArtistSelectionIsAbsent()
                .setPaintingTitle(title)
                .setPaintingPhoto("img/painting.png")
                .setPaintingDescription(description)
                .selectRandomMuseum()
                .successSubmit();
        artistInfoPage
                .checkAlertMessage("Добавлена картина: " + title);
    }

    @User
    @ApiLogin
    @Painting
    @Story("Отображение картин")
    @Feature("Отображение картины в списке работ художника")
    @Severity(SeverityLevel.BLOCKER)
    @Tags({@Tag("web")})
    @Test
    @DisplayName("WEB: Отображение картины в списке работ на странице художника")
    void shouldDisplayPaintingInArtistWorksList(PaintingJson painting) {
        String artistId = painting.artistId().toString();
        ArtistInfoPage artistInfoPage = Selenide.open(ArtistInfoPage.getUrl(artistId), ArtistInfoPage.class);
        artistInfoPage
                .checkThatPageLoaded()
                .verifyPaintingDisplayedOnArtistPage(painting.title());
    }
}