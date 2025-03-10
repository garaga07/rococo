package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.ItemsComponent;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.component.modal.PaintingModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class PaintingListPage extends BasePage<PaintingListPage> {

    public static final String URL = CFG.frontUrl() + "painting";
    private final SelenideElement emptyStateMessage = $x("//p[text()='Пока что список картин  пуст. " +
            "Чтобы пополнить коллекцию, добавьте новую картину']");
    private final SelenideElement addPaintingButton = $x("//button[text()='Добавить картину']");
    private final SelenideElement pageTitle = $x("//h2[text()='Картины']");
    private final SearchField searchField = new SearchField();
    private final ItemsComponent itemsComponent = new ItemsComponent();

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public PaintingListPage checkThatPageLoaded() {
        pageTitle.shouldBe(visible);
        addPaintingButton.shouldBe(visible);
        searchField.getSelf().shouldBe(visible);
        return this;
    }

    @Step("Check empty painting list message")
    public void checkEmptyState(String expectedText) {
        emptyStateMessage.shouldBe(visible).shouldHave(text(expectedText));
    }

    @Step("Click 'Add Painting' button")
    public PaintingModal addNewPaintingButtonClick() {
        addPaintingButton.click();
        return new PaintingModal();
    }

    @Step("Verify presence of painting: {paintingTitle}")
    public void verifyPaintingPresence(String paintingTitle) {
        itemsComponent.findItem(paintingTitle).shouldBe(visible);
    }

    @Step("Check that empty painting search results contain title: {expectedTitle} and description: {expectedDescription}")
    public void checkEmptySearchResults(String expectedTitle, String expectedDescription) {
        searchField.checkEmptySearchText(expectedTitle, expectedDescription);
    }

    @Step("Filter painting by title: {paintingTitle}")
    public PaintingListPage filterPaintingByTitle(String paintingTitle) {
        searchField.search(paintingTitle);
        return this;
    }
}