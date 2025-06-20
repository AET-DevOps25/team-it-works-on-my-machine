package de.tum.users.controller;

import de.tum.users.model.User;
import de.tum.users.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    @PostMapping(value = "/users", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createUser(@RequestBody User user) {
        var newUser = userRepository
                .findByGithubId(user.getGithubId())
                .map(existingUser -> {
                    existingUser.setToken(user.getToken());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> userRepository.save(user));
        userRepository.flush();
        return newUser.getId();
    }

    @DeleteMapping("/users/{githubId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String githubId) {
        User user = userRepository.findByGithubId(githubId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ping() {
        return "Pong from User Service\n";
    }
}
