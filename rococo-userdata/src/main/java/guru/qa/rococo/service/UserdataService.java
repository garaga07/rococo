package guru.qa.rococo.service;

import guru.qa.rococo.data.UserdataEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.UserdataJson;
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
    public void listener(@Payload UserdataJson user, ConsumerRecord<String, UserdataJson> cr) {
        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("### Kafka consumer record: {}", cr.toString());
                            UserdataEntity userDataEntity = new UserdataEntity();
                            userDataEntity.setUsername(user.username());
                            UserdataEntity userEntity = userRepository.save(userDataEntity);
                            LOG.info(
                                    "### User '{}' successfully saved to database with id: {}",
                                    user.username(),
                                    userEntity.getId()
                            );
                        }
                );
    }

    @Transactional
    public @Nonnull
    UserdataJson update(@Nonnull UserdataJson user) {
        UserdataEntity userEntity = getRequiredUser(user.username());
        userEntity.setFirstname(user.firstname());
        userEntity.setLastname(user.lastname());
        userEntity.setUsername(user.username());
        userEntity.setAvatar(user.avatar() != null ? user.avatar().getBytes(StandardCharsets.UTF_8) : null);
        UserdataEntity saved = userRepository.save(userEntity);
        return UserdataJson.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public @Nonnull
    UserdataJson getUser(@Nonnull String username) {
        return UserdataJson.fromEntity(getRequiredUser(username));
    }

    @Nonnull
    private UserdataEntity getRequiredUser(@Nonnull String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }
}