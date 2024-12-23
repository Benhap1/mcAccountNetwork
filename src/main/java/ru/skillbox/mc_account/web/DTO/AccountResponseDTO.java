package ru.skillbox.mc_account.web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.mc_account.entity.Role;
import java.util.UUID;


@Getter
@Setter
public class AccountResponseDTO {

    private UUID id;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    private String password;
    private Role role;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private String statusCode;
    private String regDate;
    private String birthDate;
    private String messagePermission;
    private String lastOnlineTime;
    private String emojiStatus;
    private String createdOn;
    private String updatedOn;
    private String deletionTimestamp;
    private boolean deleted;

    @JsonProperty("isOnline")
    private boolean online;

    private boolean blocked;
}