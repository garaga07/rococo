package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.sleep;

public class SelectField extends BaseComponent<SelectField> {

    private final ElementsCollection options = self.$$("option");

    public SelectField(@Nonnull SelenideElement self) {
        super(self);
    }

    public void selectOption(String value) {
        selectInternal(value);
    }

    public String selectRandomOption() {
        return selectInternal(null);
    }

    private String selectInternal(String value) {
        int initialOptionsCount = options.size();

        while (true) {
            // Фильтруем доступные опции и получаем их тексты
            List<SelenideElement> availableOptions = options.stream()
                    .filter(option -> !option.getText().trim().isEmpty())
                    .collect(Collectors.toList());

            if (availableOptions.isEmpty()) {
                throw new IllegalStateException("No available options found.");
            }

            // Выбираем нужную опцию (по значению или случайно)
            SelenideElement selectedOption = value != null
                    ? availableOptions.stream().filter(option -> option.getText().equals(value)).findFirst().orElse(null)
                    : availableOptions.get(new Random().nextInt(availableOptions.size()));

            if (selectedOption != null && selectedOption.exists()) {
                selectedOption.click();
                return selectedOption.getText();
            }

            // Скроллим до последней опции и даём время на подгрузку
            options.last().scrollIntoView(true).click();
            sleep(500);

            options.shouldHave(sizeGreaterThan(initialOptionsCount)
                    .because("New options failed to load. Current count: " + options.size()));

            initialOptionsCount = options.size();
        }
    }
}