package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ArtistModal extends BaseModal<ArtistModal> {

    private final SelenideElement artistNameField = ($("[name=name]"));
    private final SelenideElement imageUploader = $("[name=photo]");
    private final SelenideElement biographyField = ($("[name=biography]"));

    @Step("Set artist name: {name}")
    public ArtistModal setArtistName(String name) {
        artistNameField.setValue(name);
        return this;
    }

    @Step("Set artist biography: {biography}")
    public ArtistModal setArtistBiography(String biography) {
        biographyField.setValue(biography);
        return this;
    }

    @Step("Set artist photo: {filepath}")
    public ArtistModal setArtistPhoto(String filepath) {
        imageUploader.uploadFromClasspath(filepath);
        return this;
    }

    @Step("Check all artist validation errors")
    public void checkAllArtistErrors(String expectedNameError, String expectedBiographyError) {
        checkValidationErrors(new Object[][]{
                {artistNameField, expectedNameError},
                {biographyField, expectedBiographyError}
        });
    }
}