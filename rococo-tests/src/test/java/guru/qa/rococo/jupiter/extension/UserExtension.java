package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.UsersClient;
import guru.qa.rococo.service.impl.UsersDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;
import java.util.logging.Logger;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Logger LOGGER = Logger.getLogger(UserExtension.class.getName());
    private static final String DEFAULT_PASSWORD = "12345";

    private final UsersClient usersClient = new UsersDbClient();

    private static final ThreadLocal<UserJson> userStore = ThreadLocal.withInitial(() -> null);

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();
                        LOGGER.info(() -> "Creating test user: " + username);

                        UserJson testUser = usersClient.createUser(username, DEFAULT_PASSWORD);
                        userStore.set(testUser);

                        LOGGER.info(() -> "User created: " + testUser);
                    }
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        UserJson user = userStore.get();
        if (user != null) {
            try {
                Optional<UserJson> existingUser = usersClient.findUserByUsername(user.username());
                if (existingUser.isPresent()) {
                    usersClient.deleteUserByUsername(user.username());
                    LOGGER.info(() -> "Deleted user: " + user.username());
                } else {
                    LOGGER.warning(() -> "User already deleted or not found: " + user.username());
                }
            } catch (Exception e) {
                LOGGER.severe(() -> "Failed to delete user: " + user.username() + " due to " + e.getMessage());
            }
        }
        userStore.remove();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        UserJson user = userStore.get();
        if (user == null) {
            throw new IllegalStateException("No user was created in UserExtension");
        }
        return user;
    }

    public static void setUser(UserJson testUser) {
        userStore.set(testUser);
    }

    public static UserJson getUserJson() {
        return userStore.get();
    }
}