package de.tum.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "analysis")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp()
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "repository")
    private String repository;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    protected Analysis() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn’t be used directly
    }

    public Analysis(User user, String content, String repository) {
        this.user = user;
        this.content = content;
        this.repository = repository;
    }
}
