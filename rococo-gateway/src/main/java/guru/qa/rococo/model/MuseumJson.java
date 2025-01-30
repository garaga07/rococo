package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 50, message = "Название музея должно содержать от 3 до 50 символов")
        @NotNull(message = "Название музея не может быть пустым")
        @JsonProperty("title")
        String title,

        @Size(max = 500, message = "Описание не должно превышать 500 символов")
        @NotNull(message = "Описание обязательно для заполнения")
        @JsonProperty("description")
        String description,

        @NotNull(message = "Фото обязательно")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo,

        @NotNull(message = "Геоданные обязательны")
        @JsonProperty("geo")
        GeoJson geo
) {
}