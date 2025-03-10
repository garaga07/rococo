package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.ItemsComponent;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.component.modal.MuseumModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class MuseumListPage extends BasePage<MuseumListPage> {

    public static final String URL = CFG.frontUrl() + "museum";
    private final SelenideElement emptyStateMessage = $x("//p[text()='Пока что список картин пуст. " +
            "Чтобы пополнить коллекцию, добавьте новую картину']");
    private final SelenideElement addMuseumButton = $x("//button[text()='Добавить музей']");
    private final SelenideElement pageTitle = $x("//h2[text()='Музеи']");
    private final SearchField searchField = new SearchField();
    private final ItemsComponent itemsComponent = new ItemsComponent();

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public MuseumListPage checkThatPageLoaded() {
        pageTitle.shouldBe(visible);
        addMuseumButton.shouldBe(visible);
        searchField.getSelf().shouldBe(visible);
        return this;
    }

    @Step("Check empty museum list message")
    public void checkEmptyState(String expectedText) {
        emptyStateMessage.shouldBe(visible).shouldHave(text(expectedText));
    }

    @Step("Click 'Add Museum' button")
    public MuseumModal addNewMuseumButtonClick() {
        addMuseumButton.click();
        return new MuseumModal();
    }

    @Step("Verify presence of museum: {museumTitle}")
    public void verifyMuseumPresence(String museumTitle) {
        itemsComponent.findItem(museumTitle).shouldBe(visible);
    }

    @Step("Check that empty museum search results contain title: {expectedTitle} and description: {expectedDescription}")
    public void checkEmptySearchResults(String expectedTitle, String expectedDescription) {
        searchField.checkEmptySearchText(expectedTitle, expectedDescription);
    }

    @Step("Filter museums by title: {museumTitle}")
    public MuseumListPage filterMuseumByTitle(String museumTitle) {
        searchField.search(museumTitle);
        return this;
    }

}