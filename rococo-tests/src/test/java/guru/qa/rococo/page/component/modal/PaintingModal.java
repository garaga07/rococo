package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.SelectField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class PaintingModal extends BaseModal<PaintingModal> {

    private final SelenideElement titleField = ($("[name=title]"));
    private final SelenideElement photoInput = $("[name=content]");
    private final SelectField artistSelect = new SelectField($("[name=authorId]"));
    private final SelenideElement descriptionField = ($("[name=description]"));
    private final SelectField museumSelect = new SelectField($("[name=museumId]"));


    @Step("Set painting title: {title}")
    public PaintingModal setPaintingTitle(String title) {
        titleField.setValue(title);
        return this;
    }

    @Step("Select painting author: {author}")
    public PaintingModal selectAuthor(String author) {
        artistSelect.selectOption(author);
        return this;
    }

    @Step("Select painting museum: {museum}")
    public PaintingModal selectMuseum(String museum) {
        museumSelect.selectOption(museum);
        return this;
    }

    @Step("Select random painting author")
    public PaintingModal selectRandomAuthor() {
        artistSelect.selectRandomOption();
        return this;
    }

    @Step("Select random painting museum")
    public PaintingModal selectRandomMuseum() {
        museumSelect.selectRandomOption();
        return this;
    }

    @Step("Set painting photo: {filepath}")
    public PaintingModal setPaintingPhoto(String filepath) {
        photoInput.uploadFromClasspath(filepath);
        return this;
    }

    @Step("Set painting description: {description}")
    public PaintingModal setPaintingDescription(String description) {
        descriptionField.setValue(description);
        return this;
    }

    @Step("Check all painting validation errors")
    public void checkAllPaintingErrors(String expectedTitleError, String expectedDescriptionError) {
        checkValidationErrors(new Object[][]{
                {titleField, expectedTitleError},
                {descriptionField, expectedDescriptionError}
        });
    }

    @Step("Verify that the artist selection field is absent in the modal")
    public PaintingModal verifyArtistSelectionIsAbsent() {
        artistSelect.getSelf().shouldNot(exist);
        return this;
    }
}