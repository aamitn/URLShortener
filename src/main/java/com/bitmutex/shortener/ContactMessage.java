package com.bitmutex.shortener;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_form")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    @Lob
    private byte[] attachment;

    @Column(length = 2000)
    private String message;

    // constructors, getters, setters...

    // Additional code...

    public ContactMessage(String name, String email, String phoneNumber, SourceType sourceType, byte[] attachment, String message) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.sourceType = sourceType;
        this.attachment = attachment;
        this.message = message;
    }

    public ContactMessage() {

    }
}