package ru.skillbox.mc_account.web.DTO;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;


@Getter
@Setter
public class AccountUpdateDTO {
    private String firstName;
    private String lastName;
    private Instant birthDate;
    private String phone;
    private String about;
    private String city;
    private String country;
    private String emojiStatus;
    private String photo;
    private String profileCover;
}