package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.modal.PaintingModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class PaintingInfoPage extends BasePage<PaintingInfoPage> {

    private static final String BASE_URL = CFG.frontUrl() + "painting/";
    private final SelenideElement photo = $("[data-testid=painting-photo]");
    private final SelenideElement title = $("[data-testid=painting-title]");
    private final SelenideElement author = $("[data-testid=painting-author]");
    private final SelenideElement editButton = $("[data-testid=edit-painting]");
    private final SelenideElement description = $("[data-testid=painting-description]");
    

    public static String getUrl(String paintingId) {
        return BASE_URL + paintingId;
    }

    @Step("Check painting title is: {title}")
    public PaintingInfoPage checkTitle(String title) {
        this.title.shouldHave(text(title));
        return this;
    }

    @Step("Check painting description is: {description}")
    public PaintingInfoPage checkDescription(String description) {
        this.description.shouldHave(text(description));
        return this;
    }

    @Step("Check painting photo exist")
    @Nonnull
    public PaintingInfoPage checkPhotoExist() {
        photo.should(attributeMatching("src", "data:image.*"));
        return this;
    }


    @Step("Open painting edit form")
    public PaintingModal openEditForm() {
        editButton.click();
        return new PaintingModal();
    }

    @Override
    public PaintingInfoPage checkThatPageLoaded() {
        title.shouldBe(visible);
        description.shouldBe(visible);
        author.shouldBe(visible);
        return this;
    }
}