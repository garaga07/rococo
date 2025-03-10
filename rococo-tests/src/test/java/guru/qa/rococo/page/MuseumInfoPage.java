package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.modal.MuseumModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class MuseumInfoPage extends BasePage<MuseumInfoPage> {

    private static final String BASE_URL = CFG.frontUrl() + "museum/";
    private final SelenideElement title = $("[data-testid=museum-title]");
    private final SelenideElement location = $("[data-testid=museum-location]");
    private final SelenideElement description = $("[data-testid=museum-description]");
    private final SelenideElement photo = $("[data-testid=museum-photo]");
    private final SelenideElement editButton = $("[data-testid=edit-museum]");

    public static String getUrl(String museumId) {
        return BASE_URL + museumId;
    }

    @Step("Check museum title is: {title}")
    public MuseumInfoPage checkTitle(String title) {
        this.title.shouldHave(text(title));
        return this;
    }

    @Step("Check museum location is: {location}")
    public MuseumInfoPage checkLocation(String location) {
        this.location.shouldHave(text(location));
        return this;
    }

    @Step("Check museum description is: {description}")
    public MuseumInfoPage checkDescription(String description) {
        this.description.shouldHave(text(description));
        return this;
    }

    @Step("Check photo exist")
    @Nonnull
    public MuseumInfoPage checkPhotoExist() {
        photo.should(attributeMatching("src", "data:image.*"));
        return this;
    }


    @Step("Open museum edit form")
    public MuseumModal openEditForm() {
        editButton.click();
        return new MuseumModal();
    }

    @Override
    public MuseumInfoPage checkThatPageLoaded() {
        title.shouldBe(visible);
        location.shouldBe(visible);
        description.shouldBe(visible);
        return this;
    }
}