package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 255, message = "title: Название должно содержать от 3 до 255 символов")
        @NotNull(message = "title: Название обязательно для заполнения")
        @JsonProperty("title")
        String title,

        @Size(min = 11, max = 2000, message = "description: Описание должно содержать от 11 до 2000 символов")
        @NotNull(message = "description: Описание обязательно для заполнения")
        @JsonProperty("description")
        String description,

        @Pattern(regexp = "^data:image/.*", message = "photo: Фото должно начинаться с 'data:image/'")
        @NotNull(message = "photo: Фото обязательно для заполнения")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "photo: Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo,

        @Valid
        @NotNull(message = "geo: Геоданные обязательны для заполнения")
        @JsonProperty("geo")
        GeoJson geo
) {
        public MuseumJson(UUID id, String title, String description, String photo, GeoJson geo) {
                this.id = id;
                this.title = normalizeString(title);
                this.description = normalizeString(description);
                this.photo = photo;
                this.geo = geo;
        }

        private static String normalizeString(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? " " : trimmed;
        }
}