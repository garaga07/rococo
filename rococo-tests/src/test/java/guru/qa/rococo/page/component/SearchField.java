package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class SearchField extends BaseComponent<SearchField> {
    public SearchField(@Nonnull SelenideElement self) {
        super(self);
    }

    public SearchField() {
        super($x("//input[@type='search']"));
    }

    private final SelenideElement emptySearchContainer = $("main#page-content>section>div");

    @Step("Perform search for query {query}")
    @Nonnull
    public SearchField search(String query) {
        clearIfNotEmpty();
        self.setValue(query).pressEnter();
        return this;
    }

    @Step("Try to clear search field manually")
    @Nonnull
    public SearchField clearIfNotEmpty() {
        if (self.is(not(empty))) {
            self.doubleClick().sendKeys(Keys.BACK_SPACE);
            self.should(empty);
        }
        return this;
    }

    @Step("Check empty search message with expected title: {expectedTitle} and description: {expectedDescription}")
    public void checkEmptySearchText(String expectedTitle, String expectedDescription) {
        emptySearchContainer.shouldBe(visible);
        emptySearchContainer.$("p:first-of-type").shouldHave(text(expectedTitle));
        emptySearchContainer.$("p:last-of-type").shouldHave(text(expectedDescription));
    }
}