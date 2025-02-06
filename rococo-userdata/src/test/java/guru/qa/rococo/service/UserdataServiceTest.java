package guru.qa.rococo.service;

import guru.qa.rococo.data.UserdataEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.SameUsernameException;
import guru.qa.rococo.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserdataServiceTest {

    private static final String AVATAR_PLACEHOLDER = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";

    private UserdataService userdataService;

    @Mock
    private UserdataRepository userRepository;

    private UUID userId;
    private UserdataEntity userEntity;

    @BeforeEach
    void setUp() {
        userdataService = new UserdataService(userRepository);

        userId = UUID.randomUUID();
        userEntity = new UserdataEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");
        userEntity.setFirstname("John");
        userEntity.setLastname("Doe");
        userEntity.setAvatar(AVATAR_PLACEHOLDER.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldReturnUserByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(userEntity));

        UserJson result = userdataService.getUser("testUser");

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testUser");
        assertThat(result.firstname()).isEqualTo("John");
        assertThat(result.lastname()).isEqualTo("Doe");
        assertThat(result.avatar()).isEqualTo(AVATAR_PLACEHOLDER);
    }

    @Test
    void shouldThrowNotFoundExceptionForNonExistentUser() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userdataService.getUser("unknownUser"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("username: Пользователь не найден: unknownUser");
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserdataEntity.class))).thenReturn(userEntity);

        UserJson updatedUser = new UserJson(userId, "newUser", "John", "Doe", AVATAR_PLACEHOLDER);

        UserJson result = userdataService.update(updatedUser);

        assertThat(result.username()).isEqualTo("newUser");
        assertThat(result.avatar()).isEqualTo(AVATAR_PLACEHOLDER);

        verify(userRepository).save(any(UserdataEntity.class));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingWithoutId() {
        UserJson invalidUser = new UserJson(null, "testUser", "John", "Doe", AVATAR_PLACEHOLDER);

        assertThatThrownBy(() -> userdataService.update(invalidUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id: ID пользователя обязателен для заполнения");
    }

    @Test
    void shouldThrowSameUsernameException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.of(new UserdataEntity()));

        UserJson updatedUser = new UserJson(userId, "newUser", "John", "Doe", AVATAR_PLACEHOLDER);

        assertThatThrownBy(() -> userdataService.update(updatedUser))
                .isInstanceOf(SameUsernameException.class)
                .hasMessageContaining("username: Имя пользователя 'newUser' уже занято.");
    }
}