package guru.qa.rococo.config;

import javax.annotation.Nonnull;
import java.util.List;

public interface Config {

    static @Nonnull Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    @Nonnull
    default String projectId() {
        return "rococo";
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String userdataUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String artistUrl();

    @Nonnull
    String artistJdbcUrl();

    @Nonnull
    String museumUrl();

    @Nonnull
    String museumJdbcUrl();

    @Nonnull
    String paintingUrl();

    @Nonnull
    String paintingJdbcUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String kafkaAddress();

    default List<String> kafkaTopics() {
        return List.of("users");
    }

    String allureDockerServiceUrl();
}