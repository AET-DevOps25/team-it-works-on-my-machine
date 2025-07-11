package de.tum.users.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "github_id")
    @JsonProperty("github_id")
    private String githubId;

    @Column(name = "username", updatable = false, nullable = false, unique = true)
    private String username;

    @Column(name = "token")
    private String token;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Analysis> analysis;

    protected User() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn’t be used directly
    }

    public User(String githubId, String username, String token) {
        this.githubId = githubId;
        this.username = username;
        this.token = token;
    }
}
