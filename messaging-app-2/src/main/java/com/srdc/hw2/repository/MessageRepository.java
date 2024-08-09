package com.srdc.hw2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.srdc.hw2.model.Message;

/**
 * Repository interface for managing Message entities.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Finds messages by receiver's username with pagination.
     *
     * @param receiver the receiver's username
     * @param pageable the pagination information
     * @return a page of messages
     */
    Page<Message> findByReceiver(String receiver, Pageable pageable);

    /**
     * Finds messages by sender's username with pagination.
     *
     * @param sender the sender's username
     * @param pageable the pagination information
     * @return a page of messages
     */
    Page<Message> findBySender(String sender, Pageable pageable);

    /**
     * Nullifies the sender's username in messages.
     *
     * @param username the username to be nullified
     */
    @Modifying
    @Query("UPDATE Message m SET m.sender = '~ removed user ~' WHERE m.sender = :username")
    void nullifySender(@Param("username") String username);

    /**
     * Nullifies the receiver's username in messages.
     *
     * @param username the username to be nullified
     */
    @Modifying
    @Query("UPDATE Message m SET m.receiver = '~ removed user ~' WHERE m.receiver = :username")
    void nullifyReceiver(@Param("username") String username);

    /**
     * Finds inbox messages by a field and value with pagination.
     *
     * @param username the receiver's username
     * @param field the field to search by
     * @param value the value to search for
     * @param pageable the pagination information
     * @return a page of messages
     */
    @Query(value = "SELECT * FROM messages m WHERE " +
            "LOWER(m.receiver) = LOWER(:username) AND " +
            "((:field = 'sender' AND LOWER(m.sender) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'content' AND LOWER(m.content) LIKE LOWER(CONCAT('%', :value, '%'))))",
            countQuery = "SELECT count(*) FROM messages m WHERE " +
                    "LOWER(m.receiver) = LOWER(:username) AND " +
                    "((:field = 'sender' AND LOWER(m.sender) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'content' AND LOWER(m.content) LIKE LOWER(CONCAT('%', :value, '%'))))",
            nativeQuery = true)
    Page<Message> findInboxMessagesByFieldAndValue(
            @Param("username") String username,
            @Param("field") String field,
            @Param("value") String value,
            Pageable pageable
    );

    /**
     * Finds outbox messages by a field and value with pagination.
     *
     * @param username the sender's username
     * @param field the field to search by
     * @param value the value to search for
     * @param pageable the pagination information
     * @return a page of messages
     */
    @Query(value = "SELECT * FROM messages m WHERE " +
            "LOWER(m.sender) = LOWER(:username) AND " +
            "((:field = 'receiver' AND LOWER(m.receiver) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
            "(:field = 'content' AND LOWER(m.content) LIKE LOWER(CONCAT('%', :value, '%'))))",
            countQuery = "SELECT count(*) FROM messages m WHERE " +
                    "LOWER(m.sender) = LOWER(:username) AND " +
                    "((:field = 'receiver' AND LOWER(m.receiver) LIKE LOWER(CONCAT('%', :value, '%'))) OR " +
                    "(:field = 'content' AND LOWER(m.content) LIKE LOWER(CONCAT('%', :value, '%'))))",
            nativeQuery = true)
    Page<Message> findOutboxMessagesByFieldAndValue(
            @Param("username") String username,
            @Param("field") String field,
            @Param("value") String value,
            Pageable pageable
    );

}
