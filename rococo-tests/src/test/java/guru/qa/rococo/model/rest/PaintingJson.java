package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.painting.PaintingEntity;

import java.nio.charset.StandardCharsets;
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

        @JsonProperty("artistId")
        UUID artistId,

        @JsonProperty("museumId")
        UUID museumId
) {
        public static PaintingJson fromEntity(PaintingEntity entity) {
                return new PaintingJson(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getContent() != null ? new String(entity.getContent(), StandardCharsets.UTF_8) : null,
                        entity.getArtistId(),
                        entity.getMuseumId()
                );
        }
}