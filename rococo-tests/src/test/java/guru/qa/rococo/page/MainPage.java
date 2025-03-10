package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl();
    private final Header header = new Header();
    private final SelenideElement paintingLink = $x("//img[@alt='Ссылка на картины']");
    private final SelenideElement artistLink = $x("//img[@alt='Ссылка на художников']");
    private final SelenideElement museumLink = $x("//img[@alt='Ссылка на музеи']");

    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public MainPage checkThatPageLoaded() {
        header.getSelf().should(visible).shouldHave(text("Rococo"));
        paintingLink.shouldBe(visible);
        artistLink.shouldBe(visible);
        museumLink.shouldBe(visible);
        return this;
    }
}