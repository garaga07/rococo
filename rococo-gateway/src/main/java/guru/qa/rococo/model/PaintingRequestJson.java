package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PaintingRequestJson(
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

        @NotNull(message = "content: Фото обязательно для заполнения")
        @Pattern(regexp = "^data:image/.*", message = "content: Фото должно начинаться с 'data:image/'")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "content: Размер фото не должен превышать 1MB")
        @JsonProperty("content")
        String content,

        @Valid
        @NotNull(message = "artist: Художник обязателен для заполнения")
        @JsonProperty("artist")
        ArtistRef artist,

        @Valid
        @NotNull(message = "museum: Музей обязателен для заполнения")
        @JsonProperty("museum")
        MuseumRef museum
) {
        public PaintingRequestJson(
                UUID id,
                String title,
                String description,
                String content,
                ArtistRef artist,
                MuseumRef museum) {
                this.id = id;
                this.title = normalizeString(title);
                this.description = normalizeString(description);
                this.content = content;
                this.artist = artist;
                this.museum = museum;
        }

        private static String normalizeString(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? " " : trimmed;
        }
}