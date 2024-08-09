package com.srdc.hw2.controller;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.srdc.hw2.model.Message;
import com.srdc.hw2.repository.MessageRepository;
import com.srdc.hw2.repository.UserRepository;
import com.srdc.hw2.security.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static com.srdc.hw2.controller.UserController.activeTokens;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Get messages for the logged-in user.
     *
     * @param token    the authorization token
     * @param inout    specifies whether to fetch inbox or outbox messages ("in" or "out")
     * @param page     the page number to fetch (default is 0)
     * @param size     the size of the page (default is 10)
     * @param field    optional field to filter messages
     * @param value    optional value to filter messages by the specified field
     * @return ResponseEntity containing a page of messages or appropriate HTTP status
     */
    @GetMapping("/message")
    public ResponseEntity<Page<Message>> getMessages(@RequestHeader("Authorization") String token,
                                                     @RequestParam String inout,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String field,
                                                     @RequestParam(required = false) String value) {
        if (!activeTokens.contains(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            String username = AuthService.getUsername(token);
            Pageable paging = PageRequest.of(page, size);
            Page<Message> messages;

            if ("in".equalsIgnoreCase(inout)) {
                if (field != null && value != null) {
                    messages = messageRepository.findInboxMessagesByFieldAndValue(username, field, value, paging);
                } else {
                    messages = messageRepository.findByReceiver(username, paging);
                }
            } else if ("out".equalsIgnoreCase(inout)) {
                if (field != null && value != null) {
                    messages = messageRepository.findOutboxMessagesByFieldAndValue(username, field, value, paging);
                } else {
                    messages = messageRepository.findBySender(username, paging);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (messages.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send a message from the logged-in user.
     *
     * @param token   the authorization token
     * @param message the message object to be sent
     * @return ResponseEntity containing the created message or appropriate HTTP status
     */
    @PostMapping("/message")
    public ResponseEntity<Message> sendMessage(@RequestHeader("Authorization") String token, @RequestBody Message message) {
        if (!activeTokens.contains(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            String sender = AuthService.getUsername(token);
            if (!userRepository.existsByUsername(message.getReceiver())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if receiver does not exist
            }
            Message _message = messageRepository.save(new Message(sender, message.getReceiver(), message.getContent(), new Timestamp(System.currentTimeMillis())));
            return new ResponseEntity<>(_message, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
