package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PaintingResponseJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum")
        MuseumJson museumJson,
        @JsonProperty("artist")
        ArtistJson artistJson
        ) {

    public static @Nonnull PaintingResponseJson fromEntity(@Nonnull PaintingEntity entity,
                                                           @Nonnull ArtistJson artist,
                                                           @Nonnull MuseumJson museum) {
        return new PaintingResponseJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null && entity.getContent().length > 0 ? new String(entity.getContent(), StandardCharsets.UTF_8) : null,
                museum,
                artist
        );
    }
}