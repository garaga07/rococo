package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum")
        UUID museum,
        @JsonProperty("artist")
        UUID artist
        ) {

    public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent(),
                entity.getMuseum(),
                entity.getArtist()
        );
    }
}