package com.srdc.hw2.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.srdc.hw2.model.User;
import com.srdc.hw2.repository.UserRepository;
import com.srdc.hw2.repository.MessageRepository;
import com.srdc.hw2.security.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    // List to maintain active tokens
    static List<String> activeTokens = new ArrayList<>();

    /**
     * Log in a user and return a token.
     *
     * @param user the user object containing username and password
     * @return ResponseEntity containing the token or appropriate HTTP status
     */
    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> userData = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));

        if (userData.isPresent() && userData.get().getPassword().equals(user.getPassword())) {
            boolean isAdmin = userData.get().isAdmin();
            String token = AuthService.login(user.getUsername(), isAdmin);
            activeTokens.add(token); // Add token to the active tokens list
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Log out a user by invalidating the token.
     *
     * @param token the authorization token
     * @return ResponseEntity with appropriate HTTP status
     */
    @PostMapping("/user/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (activeTokens.contains(token)) {
            activeTokens.remove(token); // Remove token from the active tokens list
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * List all users with pagination and optional filtering.
     *
     * @param token  the authorization token
     * @param page   the page number to fetch (default is 0)
     * @param size   the size of the page (default is 10)
     * @param field  optional field to filter users
     * @param value  optional value to filter users by the specified field
     * @return ResponseEntity containing a page of users or appropriate HTTP status
     */
    @GetMapping("/user")
    public ResponseEntity<Page<User>> listUsers(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String field,
                                                @RequestParam(required = false) String value) {
        if (!activeTokens.contains(token) || !AuthService.isAdmin(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Pageable paging = PageRequest.of(page, size);
            Page<User> users;

            if (field != null && value != null) {
                users = userRepository.findUsersByFieldAndValue(field, value, paging);
            } else {
                users = userRepository.findAll(paging);
            }

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new user.
     *
     * @param token the authorization token
     * @param user  the user object to be created
     * @return ResponseEntity containing the created user or appropriate HTTP status
     */
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
        if (!activeTokens.contains(token) || !AuthService.isAdmin(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            User _user = userRepository
                    .save(new User(user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), user.getBirthdate(), user.getGender(), user.getEmail(), user.getLocation(), user.isAdmin()));
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a user by username.
     *
     * @param token    the authorization token
     * @param username the username of the user to be updated
     * @param field    the field to be updated
     * @param value    the new value for the specified field
     * @return ResponseEntity containing the updated user or appropriate HTTP status
     */
    @PutMapping("/user/{username}")
    public ResponseEntity<User> updateUser(
            @RequestHeader("Authorization") String token,
            @PathVariable("username") String username,
            @RequestParam("field") String field,
            @RequestParam("value") String value) {
        if (!activeTokens.contains(token) || !AuthService.isAdmin(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userData = Optional.ofNullable(userRepository.findByUsername(username));

        if (userData.isPresent()) {
            User _user = userData.get();
            switch (field.toLowerCase()) {
                case "username":
                    _user.setUsername(value);
                    break;
                case "password":
                    _user.setPassword(value);
                    break;
                case "name":
                    _user.setName(value);
                    break;
                case "surname":
                    _user.setSurname(value);
                    break;
                case "birthdate":
                    _user.setBirthdate(Date.valueOf(value));
                    break;
                case "gender":
                    _user.setGender(value);
                    break;
                case "email":
                    _user.setEmail(value);
                    break;
                case "location":
                    _user.setLocation(value);
                    break;
                case "isadmin":
                    _user.setAdmin(Boolean.parseBoolean(value));
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Remove a user by username.
     *
     * @param token    the authorization token
     * @param username the username of the user to be removed
     * @return ResponseEntity with appropriate HTTP status
     */
    @Transactional
    @DeleteMapping("/user/{username}")
    public ResponseEntity<HttpStatus> removeUser(@RequestHeader("Authorization") String token, @PathVariable("username") String username) {
        if (!activeTokens.contains(token) || !AuthService.isAdmin(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userData = Optional.ofNullable(userRepository.findByUsername(username));

        if (userData.isPresent()) {
            try {
                // Nullify sender and receiver in messages
                messageRepository.nullifySender(username);
                messageRepository.nullifyReceiver(username);

                // Delete the user
                userRepository.deleteByUsername(username);

                // Remove tokens associated with the username
                activeTokens.removeIf(t -> AuthService.getUsername(t).equals(username));

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Search for usernames containing a specified string.
     *
     * @param token    the authorization token
     * @param username the string to search for within usernames
     * @return ResponseEntity containing a list of matching usernames or appropriate HTTP status
     */
    @GetMapping("/user/search")
    public ResponseEntity<List<String>> searchUsernames(@RequestHeader("Authorization") String token,
                                                        @RequestParam("username") String username) {
        if (!activeTokens.contains(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<User> users = userRepository.findAll();
        List<String> usernames = users.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .map(User::getUsername)
                .collect(Collectors.toList());

        return new ResponseEntity<>(usernames, HttpStatus.OK);
    }
}
