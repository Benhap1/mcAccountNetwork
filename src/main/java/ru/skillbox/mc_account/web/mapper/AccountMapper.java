package ru.skillbox.mc_account.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.skillbox.common.events.CommonEvent;
import ru.skillbox.common.events.UserEvent;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.entity.Role;
import ru.skillbox.mc_account.web.DTO.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "role", source = "account.role")
    @Mapping(target = "regDate", source = "account.regDate", qualifiedByName = "instantToString")
    @Mapping(target = "birthDate", source = "account.birthDate", qualifiedByName = "instantToString")
    @Mapping(target = "lastOnlineTime", source = "account.lastOnlineTime", qualifiedByName = "instantToString")
    @Mapping(target = "createdOn", source = "account.createdOn", qualifiedByName = "instantToString")
    @Mapping(target = "updatedOn", source = "account.updatedOn", qualifiedByName = "instantToString")
    @Mapping(target = "deletionTimestamp", source = "account.deletionTimestamp", qualifiedByName = "instantToString")
    @Mapping(target = "password", source = "account.password")
    AccountResponseDTO toResponseDTO(Account account);

    @Mapping(target = "regDate", source = "account.regDate", qualifiedByName = "instantToString")
    @Mapping(target = "birthDate", source = "account.birthDate", qualifiedByName = "instantToString")
    @Mapping(target = "lastOnlineTime", source = "account.lastOnlineTime", qualifiedByName = "instantToString")
    @Mapping(target = "createdOn", source = "account.createdOn", qualifiedByName = "instantToString")
    @Mapping(target = "updatedOn", source = "account.updatedOn", qualifiedByName = "instantToString")
    @Mapping(target = "deletionTimestamp", source = "account.deletionTimestamp", qualifiedByName = "instantToString")
    AccountMeDTO toDTO(Account account);

    Account toEntity(AccountMeDTO accountMeDTO);

    @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "instantToString")
    @Mapping(source = "lastOnlineTime", target = "lastOnlineTime", qualifiedByName = "instantToString")
    AccountDataDTO toAccountDetailsDTO(Account account);

    @Mapping(source = "birthDate", target = "birthDate", qualifiedByName = "stringToInstant")
    @Mapping(source = "lastOnlineTime", target = "lastOnlineTime", qualifiedByName = "stringToInstant")
    Account toAccountEntity(AccountDataDTO accountDataDTO);


    default CommonEvent<UserEvent> toCommonEvent(Account account) {
        UserEvent userEvent = toUserEvent(account);
        return new CommonEvent<>(userEvent.getClass().getSimpleName(), userEvent);
    }

    @Mapping(target = "id", source = "account.id")
    @Mapping(target = "firstName", source = "account.firstName")
    @Mapping(target = "lastName", source = "account.lastName")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "password", source = "account.password")
    @Mapping(target = "role", source = "account.role", qualifiedByName = "roleToString")
    @Mapping(target = "messagePermission", source = "account.messagePermission")
    @Mapping(target = "deleted", source = "account.deleted")
    @Mapping(target = "blocked", source = "account.blocked")
    UserEvent toUserEvent(Account account);


    @Named("instantToString")
    default String instantToString(Instant instant) {
        return instant != null ? DateTimeFormatter.ISO_INSTANT.format(instant) : null;
    }

    @Named("stringToInstant")
    default Instant stringToInstant(String instantString) {
        try {
            return instantString != null ? Instant.parse(instantString) : null;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "about", source = "about")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "emojiStatus", source = "emojiStatus")
    @Mapping(target = "photo", source = "photo")
    @Mapping(target = "profileCover", source = "profileCover")
    void updateAccountFromDTO(AccountUpdateDTO accountUpdateDTO, @MappingTarget Account account);


    @Mapping(target = "id", source = "account.id")
    @Mapping(target = "firstName", source = "account.firstName")
    @Mapping(target = "lastName", source = "account.lastName")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "photo", source = "account.photo")
    @Mapping(target = "statusCode", source = "account.statusCode")
    @Mapping(target = "online", source = "account.online")
    @Mapping(target = "birthDate", source = "account.birthDate", qualifiedByName = "instantToString")
    @Mapping(target = "country", source = "account.country")
    @Mapping(target = "city", source = "account.city")
    @Mapping(target = "lastOnlineTime", source = "account.lastOnlineTime", qualifiedByName = "instantToString")
    @Mapping(target = "blocked", source = "account.blocked")
    SearchFriendsDTO toSearchFriendsDTO(Account account);
}
