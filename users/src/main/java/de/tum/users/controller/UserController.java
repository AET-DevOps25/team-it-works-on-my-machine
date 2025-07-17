package de.tum.users.controller;

import de.tum.users.model.Analysis;
import de.tum.users.model.User;
import de.tum.users.repository.AnalysisRepository;
import de.tum.users.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class UserController {
    private final UserRepository userRepository;
    private final AnalysisRepository analysisRepository;

    public UserController(UserRepository userRepository, AnalysisRepository analysisRepository) {
        this.userRepository = userRepository;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    user.setId(user.getId().substring(0, Math.min(user.getId().length(), 4)) + "...");
                    user.setToken(user.getToken()
                                    .substring(0, Math.min(user.getToken().length(), 4)) + "...");
                    return user;
                })
                .toList();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    @GetMapping("/users/{id}/analysis")
    public List<Analysis> getUserAnalysis(@PathVariable String id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return user.getAnalysis().reversed();
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

    @PostMapping(value = "/users/{id}/analysis", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String createAnalysis(@PathVariable String id, @RequestBody Analysis analysis) {
        Analysis analysisNew = userRepository
                .findById(id)
                .map(existingUser -> {
                    log.info("Adding analysis to user: {}", existingUser.getUsername());
                    log.info("Analysis: {}", analysis);
                    existingUser.getAnalysis().add(analysis);
                    analysis.setUser(existingUser);
                    userRepository.saveAndFlush(existingUser);
                    return analysisRepository.saveAndFlush(analysis);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return analysisNew.getId();
    }

    @DeleteMapping("/users/{userId}/analysis/{analysisId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnalysis(@PathVariable String userId, @PathVariable String analysisId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Analysis analysis = user.getAnalysis().stream()
                .filter(a -> a.getId().equals(analysisId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Analysis not found"));
        user.getAnalysis().remove(analysis);
        userRepository.save(user);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ping() {
        return "Pong from User Service\n";
    }
}
