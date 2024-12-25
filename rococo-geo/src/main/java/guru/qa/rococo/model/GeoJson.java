package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.GeoEntity;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record GeoJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name) {

    public static @Nonnull GeoJson fromEntity(@Nonnull GeoEntity entity) {
        return new GeoJson(
                entity.getId(),
                entity.getName()
        );
    }
}