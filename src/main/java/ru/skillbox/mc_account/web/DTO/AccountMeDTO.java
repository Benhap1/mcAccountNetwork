package ru.skillbox.mc_account.web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class AccountMeDTO {

    private UUID id;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    @Schema(description = "Email address of the user", example = "string@mail.ru")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String city;
    private String country;
    private String statusCode;

    @Schema(description = "Registration date", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String regDate;

    @Schema(description = "Birth date", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String birthDate;

    private String messagePermission;

    @Schema(description = "Last online time", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String lastOnlineTime;

    private String emojiStatus;

    @Schema(description = "Created on", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String createdOn;

    @Schema(description = "Updated on", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String updatedOn;

    @Schema(description = "Deletion timestamp", example = "2023-10-20T18:51:58Z", type = "string", format = "date-time")
    private String deletionTimestamp;

    private boolean deleted;

    @JsonProperty("isOnline")
    private boolean online;

    private boolean blocked;
}
