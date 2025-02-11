package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8090/";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://userdata.rococo.dc:8285/";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-userdata";
    }

    @NotNull
    @Override
    public String artistUrl() {
        return "http://artist.rococo.dc:8282/";
    }

    @NotNull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-artist";
    }

    @NotNull
    @Override
    public String museumUrl() {
        return "http://museum.rococo.dc:8283/";
    }

    @NotNull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-museum";
    }

    @NotNull
    @Override
    public String paintingUrl() {
        return "http://painting.rococo.dc:8284/";
    }

    @NotNull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-painting";
    }
}