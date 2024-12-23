package ru.skillbox.mc_account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private String statusCode;
    private Instant regDate;
    private Instant birthDate;
    private String messagePermission;
    private Instant lastOnlineTime;
    private String emojiStatus;
    private Instant createdOn;
    private Instant updatedOn;
    private Instant deletionTimestamp;
    private boolean deleted;
    private boolean online;
    private boolean blocked;
    public Account() {
        this.id = UUID.randomUUID();
    }

    }
