package guru.qa.rococo.test.kafka;

import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.rococo.jupiter.annotation.meta.KafkaTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.Waiter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@KafkaTest
public class UserdataKafkaTest {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);
    private final UserdataUserRepositoryHibernate userRepository = new UserdataUserRepositoryHibernate();

    @Test
    void userShouldBeSavedToDatabaseAfterKafkaProcessing() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        // Регистрация пользователя через Auth API
        authApi.requestRegisterForm().execute();
        authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")).execute();

        // Ожидаем появления пользователя в БД `userdata`
        UserJson userFromDb = Waiter.waitForCondition(
                () -> userRepository.findByUsername(username)
                        .map(UserJson::fromEntity)
                        .orElse(null),
                10000L
        );

        // Проверяем, что пользователь появился в `userdata`
        Assertions.assertNotNull(userFromDb, "Пользователь не найден в БД `userdata`");
        Assertions.assertEquals(username, userFromDb.username());
    }
}