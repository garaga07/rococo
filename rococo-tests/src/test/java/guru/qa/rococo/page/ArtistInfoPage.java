package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.ItemsComponent;
import guru.qa.rococo.page.component.modal.ArtistModal;
import guru.qa.rococo.page.component.modal.PaintingModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ArtistInfoPage extends BasePage<ArtistInfoPage> {

    private static final String BASE_URL = CFG.frontUrl() + "artist/";
    private final SelenideElement name = $("[data-testid=artist-name]");
    private final SelenideElement biography = $("[data-testid=artist-biography]");
    private final SelenideElement photo = $("[data-testid=artist-photo]");
    private final SelenideElement editButton = $("[data-testid=edit-artist]");
    private final SelenideElement addPaintingButton = $x("//button[text()='Добавить картину']");
    private final ItemsComponent itemsComponent = new ItemsComponent();

    public static String getUrl(String artistId) {
        return BASE_URL + artistId;
    }

    @Step("Check artist name is: {name}")
    public ArtistInfoPage checkName(String name) {
        this.name.shouldHave(text(name));
        return this;
    }

    @Step("Check artist biography is: {biography}")
    public ArtistInfoPage checkBiography(String biography) {
        this.biography.shouldHave(text(biography));
        return this;
    }

    @Step("Check photo exist")
    @Nonnull
    public ArtistInfoPage checkPhotoExist() {
        photo.should(attributeMatching("src", "data:image.*"));
        return this;
    }


    @Step("Open artist edit form")
    public ArtistModal openEditForm() {
        editButton.click();
        return new ArtistModal();
    }

    @Override
    public ArtistInfoPage checkThatPageLoaded() {
        name.shouldBe(visible);
        biography.shouldBe(visible);
        return this;
    }

    @Step("Click 'Add Painting' button")
    public PaintingModal addNewPaintingButtonClick() {
        addPaintingButton.click();
        return new PaintingModal();
    }

    @Step("Verify that painting '{paintingTitle}' is displayed on the artist's detail page")
    public void verifyPaintingDisplayedOnArtistPage(String paintingTitle) {
        itemsComponent.findItem(paintingTitle).shouldBe(visible);
    }
}