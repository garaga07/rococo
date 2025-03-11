package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.BaseComponent;
import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public abstract class BaseModal<T extends BaseComponent> extends BaseComponent<T> {

    private final SelenideElement submitFormButton = $("button[type=submit]");

    public BaseModal() {
        super($("[data-testid=modal-component]"));
    }

    @Step("Success submit form")
    @SuppressWarnings("unchecked")
    public T successSubmit() {
        submitFormButton.click();
        self.shouldNotBe(visible);
        return (T) this;
    }

    @Step("Submit form with error")
    @SuppressWarnings("unchecked")
    public T errorSubmit() {
        submitFormButton.click();
        return (T) this;
    }

    @Step("Check multiple validation errors")
    public void checkValidationErrors(Object[][] fieldErrorPairs) {
        SoftAssertions softly = new SoftAssertions();

        for (Object[] pair : fieldErrorPairs) {
            SelenideElement field = (SelenideElement) pair[0];
            String expectedError = (String) pair[1];

            softly.assertThat(field.sibling(0).shouldBe(visible).getText())
                    .as("Ошибка в поле")
                    .isEqualTo(expectedError);
        }

        softly.assertAll();
    }
}