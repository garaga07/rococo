package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 50, message = "Имя должно содержать от 3 до 50 символов")
        @NotNull(message = "Имя не может быть пустым")
        @JsonProperty("name")
        String name,

        @Size(max = 255, message = "Биография не должна превышать 255 символов")
        @NotNull(message = "Биография обязательна для заполнения")
        @JsonProperty("biography")
        String biography,

        @NotNull(message = "Фото обязательно")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo) {
}