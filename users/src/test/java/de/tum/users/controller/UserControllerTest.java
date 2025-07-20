package de.tum.users.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.tum.users.model.Analysis;
import de.tum.users.model.User;
import de.tum.users.repository.AnalysisRepository;
import de.tum.users.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class UserControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private UserController userController;

    private User user;
    private Analysis analysis;

    @BeforeEach
    void setUp() {
        user = new User("ghid", "username", "token");
        user.setId("12345678");
        user.setAnalysis(new ArrayList<>());
        analysis = new Analysis(user, "content", "repo");
        analysis.setId("anid");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userController.getAllUsers();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getId().endsWith("..."));
        assertTrue(result.get(0).getToken().endsWith("..."));
    }

    @Test
    void testCreateUserNew() {
        when(userRepository.findByGithubId("ghid")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        String id = userController.createUser(user);
        assertEquals("12345678", id);
        verify(userRepository).flush();
    }

    @Test
    void testCreateUserExisting() {
        User existing = new User("ghid", "username", "oldtoken");
        existing.setId("12345678");
        when(userRepository.findByGithubId("ghid")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        String id = userController.createUser(user);
        assertEquals("12345678", id);
        verify(userRepository).flush();
    }

    @Test
    void testCreateAnalysisUserFound() {
        when(userRepository.findById("12345678")).thenReturn(Optional.of(user));
        when(analysisRepository.saveAndFlush(any(Analysis.class))).thenReturn(analysis);
        String result = userController.createAnalysis("12345678", analysis);
        assertEquals("anid", result);
    }

    @Test
    void testCreateAnalysisUserNotFound() {
        when(userRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> userController.createAnalysis("notfound", analysis));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeleteAnalysis() {
        user.getAnalysis().add(analysis);
        when(userRepository.findById("12345678")).thenReturn(Optional.of(user));
        userController.deleteAnalysis("12345678", "anid");
        assertTrue(user.getAnalysis().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void testDeleteAnalysisUserNotFound() {
        when(userRepository.findById("notfound")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userController.deleteAnalysis("notfound", "anid"));
    }

    @Test
    void testDeleteAnalysisAnalysisNotFound() {
        when(userRepository.findById("12345678")).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> userController.deleteAnalysis("12345678", "notfound"));
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById("12345678")).thenReturn(Optional.of(user));
        userController.deleteUser("12345678");
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById("notfound")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userController.deleteUser("notfound"));
    }

    @Test
    void testPing() {
        String result = userController.ping();
        assertEquals("Pong from User Service\n", result);
    }
}
