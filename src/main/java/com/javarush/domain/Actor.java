package com.javarush.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(schema = "movie", name = "actor")
public class Actor {
    @Id
    @Column(name = "actor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lustUpdate;

    @ManyToMany
    @JoinTable(name = "film_actor",
    joinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "actor_id"),
    inverseJoinColumns = @JoinColumn(name = "film_id", referencedColumnName = "film_id"))
    private Set<Film> films;

    public Set<Film> getFilms() {
        return films;
    }

    public void setFilms(Set<Film> films) {
        this.films = films;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getLustUpdate() {
        return lustUpdate;
    }

    public void setLustUpdate(LocalDateTime lustUpdate) {
        this.lustUpdate = lustUpdate;
    }
}
