package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.UserdataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserdataRepository extends JpaRepository<UserdataEntity, UUID> {

    @Nonnull
    Optional<UserdataEntity> findByUsername(@Nonnull String username);
}