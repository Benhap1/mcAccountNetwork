package ru.skillbox.mc_account.web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
public class SearchFriendsDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String statusCode;

    @JsonProperty("isOnline")
    private Boolean online;
    private String birthDate;
    private String country;
    private String city;
    private String lastOnlineTime;
    private boolean blocked;
}