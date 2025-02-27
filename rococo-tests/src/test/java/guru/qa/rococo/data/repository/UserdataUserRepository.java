package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.userdata.UserdataEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {

    @Nonnull
    UserdataEntity create(UserdataEntity user);

    @Nonnull
    UserdataEntity update(UserdataEntity user);

    @Nonnull
    Optional<UserdataEntity> findById(UUID id);

    @Nonnull
    Optional<UserdataEntity> findByUsername(String username);

    void delete(UserdataEntity user);
}