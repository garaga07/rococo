package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.museum.MuseumEntity;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("photo") String photo,
        @JsonProperty("geo") GeoJson geo
) {
    public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
        return new MuseumJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                GeoJson.fromEntity(entity.getGeo())
        );
    }
}