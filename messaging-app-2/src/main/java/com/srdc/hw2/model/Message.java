package com.srdc.hw2.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a message entity stored in the database.
 */
@Entity
@Table(name = "messages")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    /**
     * Default constructor for JPA.
     */
    public Message() {}

    /**
     * Parameterized constructor for creating a message.
     *
     * @param sender    the sender's username
     * @param receiver  the receiver's username
     * @param content   the content of the message
     * @param timestamp the timestamp of when the message was sent
     */
    public Message(String sender, String receiver, String content, Timestamp timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() { return sender; }

    public String getReceiver() { return receiver; }

    public String getContent() { return content; }

    public Timestamp getTimestamp() { return timestamp; }

    public void setSender(String sender) { this.sender = sender; }

    public void setReceiver(String receiver) { this.receiver = receiver; }

    public void setContent(String content) { this.content = content; }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Message [id=" + id + ", sender=" + sender + ", receiver=" + receiver + ", content=" + content + ", timestamp=" + timestamp + "]";
    }
}
