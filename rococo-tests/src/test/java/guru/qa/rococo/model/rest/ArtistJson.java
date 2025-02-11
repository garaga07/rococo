package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.artist.ArtistEntity;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo) {

        public static @Nonnull ArtistJson fromEntity(@Nonnull ArtistEntity entity) {
                return new ArtistJson(
                        entity.getId(),
                        entity.getName(),
                        entity.getBiography(),
                        entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null
                );
        }
}