package guru.qa.rococo.controller;
import guru.qa.rococo.model.UserdataJson;

import guru.qa.rococo.service.UserdataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserdataController {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataController.class);

    private final UserdataService userService;

    @Autowired
    public UserdataController(UserdataService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public UserdataJson currentUser(@RequestParam String username) {
        return userService.getUser(username);
    }

    @PatchMapping()
    public UserdataJson updateUserInfo(@RequestBody UserdataJson user) {
        return userService.update(user);
    }
}