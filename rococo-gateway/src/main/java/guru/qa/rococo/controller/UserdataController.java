package guru.qa.rococo.controller;

import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.RestUserDataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserdataController {

    private final RestUserDataClient restUserDataClient;

    @Autowired
    public UserdataController(RestUserDataClient restUserDataClient) {
        this.restUserDataClient = restUserDataClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("JWT token is missing");
        }
        String username = jwt.getClaim("sub");
        return restUserDataClient.getUser(username);
    }

    @PatchMapping
    public UserJson updateUser(@Valid @RequestBody UserJson user) {
        return restUserDataClient.updateUserInfo(user);
    }
}