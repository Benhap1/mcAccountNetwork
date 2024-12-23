package ru.skillbox.mc_account.web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AccountDataDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private String statusCode;
    private String birthDate;
    private String messagePermission;
    private String lastOnlineTime;
    private String emojiStatus;
    private boolean deleted;

    @JsonProperty("isOnline")
    private boolean online;
    private boolean blocked;
}