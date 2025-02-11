//package guru.qa.rococo.page;
//
//import guru.qa.rococo.page.component.Header;
//import io.qameta.allure.Step;
//
//import javax.annotation.Nonnull;
//
//import static com.codeborne.selenide.Condition.text;
//import static com.codeborne.selenide.Condition.visible;
//
//public class MainPage extends BasePage<MainPage> {
//
//    public static final String URL = CFG.frontUrl() + "main";
//
//    protected final Header header = new Header();
//
//    @Nonnull
//    public Header getHeader() {
//        return header;
//    }
//
//    @Step("Check that page is loaded")
//    @Override
//    @Nonnull
//    public MainPage checkThatPageLoaded() {
//        header.getSelf().should(visible).shouldHave(text("Rococo"));
//        return this;
//    }
//}
