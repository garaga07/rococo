package guru.qa.rococo.controller;
import guru.qa.rococo.model.UserJson;

import guru.qa.rococo.service.UserdataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/user")
public class UserdataController {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataController.class);

    private final UserdataService userService;

    @Autowired
    public UserdataController(UserdataService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public UserJson currentUser(@RequestParam String username) {
        return userService.getUser(username);
    }

    @PatchMapping()
    public UserJson updateUserInfo(@RequestBody UserJson user) {
        return userService.update(user);
    }
}