package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    private final SelenideElement alert = $x("//div[@data-testid='toast']/div[1]");

    public abstract T checkThatPageLoaded();

    @Step("Check that alert message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(visible).should(text(expectedText));
        return (T) this;
    }
}