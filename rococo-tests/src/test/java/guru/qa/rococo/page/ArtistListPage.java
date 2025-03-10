package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.ItemsComponent;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.component.modal.ArtistModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class ArtistListPage extends BasePage<ArtistListPage> {

    public static final String URL = CFG.frontUrl() + "artist";
    private final SelenideElement emptyStateMessage = $x("//p[text()='Пока что список художников пуст. " +
            "Чтобы пополнить коллекцию, добавьте нового художника']");
    private final SelenideElement addArtistButton = $x("//button[text()='Добавить художника']");
    private final SelenideElement pageTitle = $x("//h2[text()='Художники']");
    private final SearchField searchField = new SearchField();
    private final ItemsComponent itemsComponent = new ItemsComponent();

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public ArtistListPage checkThatPageLoaded() {
        pageTitle.shouldBe(visible);
        addArtistButton.shouldBe(visible);
        searchField.getSelf().shouldBe(visible);
        return this;
    }

    @Step("Check empty artist list message")
    public void checkEmptyState(String expectedText) {
        emptyStateMessage.shouldBe(visible).shouldHave(text(expectedText));
    }

    @Step("Click 'Add Artist' button")
    public ArtistModal addNewArtistButtonClick() {
        addArtistButton.click();
        return new ArtistModal();
    }

    @Step("Verify presence of artist: {artistName}")
    public void verifyArtistPresence(String artistName) {
        itemsComponent.findItem(artistName).shouldBe(visible);
    }

    @Step("Check that empty artist search results contain title: {expectedTitle} and description: {expectedDescription}")
    public void checkEmptySearchResults(String expectedTitle, String expectedDescription) {
        searchField.checkEmptySearchText(expectedTitle, expectedDescription);
    }

    @Step("Filter artists by name: {artistName}")
    public ArtistListPage filterArtistByName(String artistName) {
        searchField.search(artistName);
        return this;
    }
}