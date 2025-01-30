package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 50, message = "Название картины должно содержать от 3 до 50 символов")
        @NotNull(message = "Название картины не может быть пустым")
        @JsonProperty("title")
        String title,

        @Size(max = 500, message = "Описание не должно превышать 500 символов")
        @NotNull(message = "Описание обязательно для заполнения")
        @JsonProperty("description")
        String description,

        @NotNull(message = "Изображение картины обязательно")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Размер изображения не должен превышать 1MB")
        @JsonProperty("content")
        String content,

        @NotNull(message = "Музей обязателен")
        @JsonProperty("museum")
        MuseumJson museumJson,

        @NotNull(message = "Художник обязателен")
        @JsonProperty("artist")
        ArtistJson artistJson
) {
}