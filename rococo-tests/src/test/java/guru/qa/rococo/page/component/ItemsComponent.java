package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class ItemsComponent {

    private final ElementsCollection items = $$("ul.grid > li");

    public SelenideElement findItem(String itemName) {
        // Ждём, пока появится хотя бы один элемент в списке
        items.shouldBe(sizeGreaterThan(0), Duration.ofSeconds(8));
        // Ищем элемент по тексту, скроллим к нему и проверяем, что он видим
        return items.findBy(text(itemName)).scrollIntoView(true).shouldBe(visible, Duration.ofSeconds(8));
    }
}