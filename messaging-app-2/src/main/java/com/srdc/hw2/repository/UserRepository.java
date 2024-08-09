package com.srdc.hw2.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.srdc.hw2.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the user with the specified username
     */
    User findByUsername(String username);

    /**
     * Deletes a user by username.
     *
     * @param username the username of the user to be deleted
     */
    @Transactional
    void deleteByUsername(String username);

    /**
     * Checks if a user exists by username.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds all users with pagination.
     *
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Finds users by a field and value with pagination.
     *
     * @param field the field to search by
     * @param value the value to search for
     * @param pageable the pagination information
     * @return a page of users
     */
    @Query(value = "SELECT * FROM users u WHERE " +
            "(:field = 'username' AND LOWER(u.username) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'name' AND LOWER(u.name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'surname' AND LOWER(u.surname) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'email' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'location' AND LOWER(u.location) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'gender' AND LOWER(u.gender) = LOWER(:value)) OR " +
            "(:field = 'birthdate' AND CAST(u.birthdate AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
            "(:field = 'isadmin' AND ((u.is_admin = TRUE AND LOWER(:value) = 'true') OR (u.is_admin = FALSE AND LOWER(:value) = 'false')))",
            countQuery = "SELECT count(*) FROM users u WHERE " +
                    "(:field = 'username' AND LOWER(u.username) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'name' AND LOWER(u.name) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'surname' AND LOWER(u.surname) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'email' AND LOWER(u.email) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'location' AND LOWER(u.location) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'gender' AND LOWER(u.gender) = LOWER(:value)) OR " +
                    "(:field = 'birthdate' AND CAST(u.birthdate AS CHAR) LIKE CONCAT('%', :value, '%')) OR " +
                    "(:field = 'isadmin' AND ((u.is_admin = TRUE AND LOWER(:value) = 'true') OR (u.is_admin = FALSE AND LOWER(:value) = 'false')))",
            nativeQuery = true)
    Page<User> findUsersByFieldAndValue(
            @Param("field") String field,
            @Param("value") String value,
            Pageable pageable
    );

}
