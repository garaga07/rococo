package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.SelectField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumModal extends BaseModal<MuseumModal> {

    private final SelenideElement titleField = ($("[name=title]"));
    private final SelectField countrySelect = new SelectField($("[name=countryId]"));
    private final SelenideElement cityField = ($("[name=city]"));
    private final SelenideElement photoInput = $("[name=photo]");
    private final SelenideElement descriptionField = ($("[name=description]"));


    @Step("Set museum title: {title}")
    public MuseumModal setMuseumTitle(String title) {
        titleField.setValue(title);
        return this;
    }

    @Step("Select museum country: {country}")
    public MuseumModal selectCountry(String country) {
        countrySelect.selectOption(country);
        return this;
    }

    @Step("Select random museum country")
    public MuseumModal selectRandomCountry() {
        countrySelect.selectRandomOption();
        return this;
    }

    @Step("Set museum city: {city}")
    public MuseumModal setMuseumCity(String city) {
        cityField.setValue(city);
        return this;
    }

    @Step("Set museum photo: {filepath}")
    public MuseumModal setMuseumPhoto(String filepath) {
        photoInput.uploadFromClasspath(filepath);
        return this;
    }

    @Step("Set museum description: {description}")
    public MuseumModal setMuseumDescription(String description) {
        descriptionField.setValue(description);
        return this;
    }

    @Step("Check city error message: {error}")
    public MuseumModal checkMuseumCityError(String error) {
        cityField.sibling(0).shouldBe(visible).shouldHave(text(error));
        return this;
    }

    @Step("Check description error message: {error}")
    public MuseumModal checkMuseumDescriptionError(String error) {
        descriptionField.sibling(0).shouldBe(visible).shouldHave(text(error));
        return this;
    }

    @Step("Check title error message: {error}")
    public MuseumModal checkMuseumTitleError(String error) {
        titleField.sibling(0).shouldBe(visible).shouldHave(text(error));
        return this;
    }
}