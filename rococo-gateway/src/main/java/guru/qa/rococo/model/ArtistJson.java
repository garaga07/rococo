package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.*;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 255, message = "name: Имя должно содержать от 3 до 255 символов")
        @NotNull(message = "name: Имя обязательно для заполнения")
        @JsonProperty("name")
        String name,

        @Size(min = 11, max = 2000, message = "biography: Биография должна содержать от 11 до 2000 символов")
        @NotNull(message = "biography: Биография обязательна для заполнения")
        @JsonProperty("biography")
        String biography,

        @Pattern(regexp = "^data:image/.*", message = "photo: Фото должно начинаться с 'data:image/'")
        @NotNull(message = "photo: Фото обязательно для заполнения")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "photo: Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo) {

        @JsonCreator
        public ArtistJson(
                @JsonProperty("id") UUID id,
                @JsonProperty("name") String name,
                @JsonProperty("biography") String biography,
                @JsonProperty("photo") String photo) {
                this.id = id;
                this.name = normalizeString(name);
                this.biography = normalizeString(biography);
                this.photo = photo;
        }

        private static String normalizeString(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? " " : trimmed;
        }
}