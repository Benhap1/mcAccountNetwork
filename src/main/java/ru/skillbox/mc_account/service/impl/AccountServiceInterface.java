package ru.skillbox.mc_account.service.impl;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.skillbox.mc_account.web.DTO.*;

import java.util.Map;
import java.util.UUID;

@Validated
public interface AccountServiceInterface {

    AccountMeDTO createAccount(@Valid AccountMeDTO accountMeDTO);

    AccountResponseDTO getAccount(String email);

    AccountMeDTO getAccountMe(String email);

    AccountMeDTO updateAccountMe(AccountUpdateDTO accountUpdateDTO);

    void markAccountAsDeleted(String email);

    void markAccountAsDeleted(UUID id);

    void markAccountAsBlocked(UUID id);

    AccountDataDTO getAccountById(UUID id);

    Map<String, Object> searchAccounts(Map<String, String> allParams);
    long getTotalAccountsCount();

    void processUUID(String uuid);
}
