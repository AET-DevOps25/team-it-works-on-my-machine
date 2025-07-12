package de.tum.users.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.tum.users.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserRepositoryTest {
    @Test
    void testFindByGithubId() {
        UserRepository repo = mock(UserRepository.class);
        User user = new User("ghid", "username", "token");
        when(repo.findByGithubId("ghid")).thenReturn(Optional.of(user));
        Optional<User> result = repo.findByGithubId("ghid");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
}
