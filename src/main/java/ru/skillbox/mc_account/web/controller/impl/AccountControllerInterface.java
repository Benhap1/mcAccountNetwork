package ru.skillbox.mc_account.web.controller.impl;

import org.springframework.security.core.Authentication;
import ru.skillbox.mc_account.web.DTO.AccountDataDTO;
import ru.skillbox.mc_account.web.DTO.AccountMeDTO;
import ru.skillbox.mc_account.web.DTO.AccountResponseDTO;
import ru.skillbox.mc_account.web.DTO.AccountUpdateDTO;

import java.util.Map;
import java.util.UUID;

public interface AccountControllerInterface {

    AccountMeDTO createAccount(AccountMeDTO accountMeDTO);

    AccountResponseDTO getAccount(String email);

    AccountMeDTO updateAccountMe(AccountUpdateDTO accountUpdateDTO);

    AccountMeDTO getCurrentAccount(Authentication authentication);

    void markAccountAsDeleted(Authentication authentication);

    AccountDataDTO getAccountById(UUID id);

    void markAccountAsDeletedById(UUID id);

    void markAccountAsBlockedById(UUID id);

    Map<String, Object> searchAccounts(Map<String, String> allParams);

    Map<String, Object> searchByStatusCode(Map<String, String> allParams);

    long getTotalAccountsCount();

    void receiveUUIDFromPath(String uuid);
}

