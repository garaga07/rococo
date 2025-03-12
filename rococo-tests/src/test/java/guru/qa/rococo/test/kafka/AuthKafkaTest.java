package guru.qa.rococo.test.kafka;


import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.meta.KafkaTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.KafkaService;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@KafkaTest
public class AuthKafkaTest {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);

    @Test
    void userShouldBeProducedToKafka() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        UserJson userFromKafka = KafkaService.getUser(username);
        Assertions.assertEquals(
                username,
                userFromKafka.username()
        );
    }
}