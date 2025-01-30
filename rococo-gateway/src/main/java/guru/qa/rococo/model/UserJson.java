package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,

        @NotNull(message = "Имя пользователя не может быть пустым")
        @Size(min = 3, max = 30, message = "Имя пользователя должно быть от 3 до 30 символов")
        @JsonProperty("username")
        String username,

        @Size(max = 30, message = "Имя не может быть длиннее 30 символов")
        @JsonProperty("firstname")
        String firstname,

        @Size(max = 50, message = "Фамилия не может быть длиннее 50 символов")
        @JsonProperty("lastname")
        String lastname,

        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Аватар не должен превышать 1MB")
        @JsonProperty("avatar")
        String avatar
) {
}