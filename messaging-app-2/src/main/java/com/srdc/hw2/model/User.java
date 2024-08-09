package com.srdc.hw2.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Represents a user entity stored in the database.
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birthdate")
    private Date birthdate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @Column(name = "location")
    private String location;

    @Column(name = "isAdmin")
    private boolean isAdmin;

    /**
     * Default constructor for JPA.
     */
    public User() {}

    /**
     * Parameterized constructor for creating a user.
     *
     * @param username the user's username
     * @param password the user's password
     * @param name     the user's first name
     * @param surname  the user's last name
     * @param birthdate the user's birthdate
     * @param gender   the user's gender
     * @param email    the user's email address
     * @param location the user's location
     * @param isAdmin  whether the user is an admin
     */
    public User(String username, String password, String name, String surname, Date birthdate, String gender, String email, String location, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
        this.location = location;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getName() { return name; }

    public String getSurname() { return surname; }

    public Date getBirthdate() { return birthdate; }

    public String getGender() { return gender; }

    public String getEmail() { return email; }

    public String getLocation() { return location; }

    public boolean isAdmin() { return isAdmin; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setName(String name) { this.name = name; }

    public void setSurname(String surname) { this.surname = surname; }

    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }

    public void setGender(String gender) { this.gender = gender; }

    public void setEmail(String email) { this.email = email; }

    public void setLocation(String location) { this.location = location; }

    public void setAdmin(boolean admin) { this.isAdmin = admin; }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", name=" + name + ", surname=" + surname + ", birthdate=" + birthdate + ", gender=" + gender + ", email=" + email + ", location=" + location + ", isAdmin=" + isAdmin + "]";
    }
}
