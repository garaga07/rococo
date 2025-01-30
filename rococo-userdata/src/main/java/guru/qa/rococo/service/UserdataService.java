package guru.qa.rococo.service;

import guru.qa.rococo.data.UserdataEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.SameUsernameException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class UserdataService {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataService.class);

    private final UserdataRepository userRepository;

    @Autowired
    public UserdataService(UserdataRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exists in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("### Kafka consumer record: {}", cr.toString());
                            UserdataEntity newUser = new UserdataEntity();
                            newUser.setUsername(user.username());
                            UserdataEntity savedUser = userRepository.save(newUser);
                            LOG.info("### User '{}' saved to DB with id: {}", user.username(), savedUser.getId());
                        }
                );
    }

    @Transactional
    public @Nonnull
    UserJson update(@Nonnull UserJson user) {
        UserdataEntity userEntity = userRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("User not found by id: " + user.id()));

        if (!Objects.equals(userEntity.getUsername(), user.username())) {
            if (userRepository.findByUsername(user.username()).isPresent()) {
                throw new SameUsernameException("Username '" + user.username() + "' is already taken.");
            }
            userEntity.setUsername(user.username());
        }

        userEntity.setFirstname(user.firstname());
        userEntity.setLastname(user.lastname());
        userEntity.setAvatar(user.avatar() != null ? user.avatar().getBytes(StandardCharsets.UTF_8) : null);

        UserdataEntity saved = userRepository.save(userEntity);
        return UserJson.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public @Nonnull
    UserJson getUser(@Nonnull String username) {
        return userRepository.findByUsername(username)
                .map(UserJson::fromEntity)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}