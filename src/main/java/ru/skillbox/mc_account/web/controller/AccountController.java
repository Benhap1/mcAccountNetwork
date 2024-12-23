package ru.skillbox.mc_account.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.mc_account.service.AccountService;
import ru.skillbox.mc_account.web.DTO.AccountDataDTO;
import ru.skillbox.mc_account.web.DTO.AccountMeDTO;
import ru.skillbox.mc_account.web.DTO.AccountResponseDTO;
import ru.skillbox.mc_account.web.DTO.AccountUpdateDTO;
import ru.skillbox.mc_account.web.controller.impl.AccountControllerInterface;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController implements AccountControllerInterface {

    private final AccountService accountService;

    @Operation(summary = "Создание нового аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Аккаунт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка в запросе")
    })
    @PostMapping
    @Override
    public AccountMeDTO createAccount(@Valid @RequestBody AccountMeDTO accountMeDTO) {
        return accountService.createAccount(accountMeDTO);
    }


    @Operation(
            summary = "Получение аккаунта по email",
            parameters = {
                    @Parameter(name = "email", description = "Email адрес", required = true, in = ParameterIn.QUERY)
            }
    )
    @GetMapping
    @Override
    public AccountResponseDTO getAccount(@RequestParam String email) {
        return accountService.getAccount(email);
    }


    @Operation(summary = "Обновление аккаунта")
    @PutMapping("/me")
    @Override
    public AccountMeDTO updateAccountMe(@RequestBody AccountUpdateDTO accountUpdateDTO) {
        return accountService.updateAccountMe(accountUpdateDTO);
    }


    @Operation(summary = "Получение информации о текущем аккаунте")
    @GetMapping("/me")
    @Override
    public AccountMeDTO getCurrentAccount(Authentication authentication) {
        return accountService.getAccountMe(authentication.getName());
    }


    @Operation(summary = "Пометить текущий аккаунт как удалённый")
    @DeleteMapping("/me")
    @Override
    public void markAccountAsDeleted(Authentication authentication) {
        accountService.markAccountAsDeleted(authentication.getName());
    }


    @Operation(summary = "Получение аккаунта по ID")
    @GetMapping("/{id}")
    @Override
    public AccountDataDTO getAccountById(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }


    @Operation(summary = "Пометить аккаунт как удалённый по ID")
    @DeleteMapping("/{id}")
    @Override
    public void markAccountAsDeletedById(@PathVariable UUID id) {
        accountService.markAccountAsDeleted(id);
    }


    @Operation(summary = "Пометить аккаунт как заблокированный по ID")
    @PatchMapping("/{id}")
    @Override
    public void markAccountAsBlockedById(@PathVariable UUID id) {
        accountService.markAccountAsBlocked(id);
    }

    @Operation(
            summary = "Глобальный поиск аккаунта по ключевым словам",
            parameters = {
                    @Parameter(name = "author", description = "Автор", in = ParameterIn.QUERY),
                    @Parameter(name = "ids", description = "Список ID", in = ParameterIn.QUERY, example = "id1,id2,id3"),
                    @Parameter(name = "firstName", description = "Имя", in = ParameterIn.QUERY),
                    @Parameter(name = "lastName", description = "Фамилия", in = ParameterIn.QUERY),
                    @Parameter(name = "ageFrom", description = "Минимальный возраст", in = ParameterIn.QUERY),
                    @Parameter(name = "ageTo", description = "Максимальный возраст", in = ParameterIn.QUERY),
                    @Parameter(name = "country", description = "Страна", in = ParameterIn.QUERY),
                    @Parameter(name = "city", description = "Город", in = ParameterIn.QUERY),
                    @Parameter(name = "statusCode", description = "Код статуса", in = ParameterIn.QUERY),
                    @Parameter(name = "isDeleted", description = "Удален", in = ParameterIn.QUERY, schema = @Schema(type = "boolean"))
            }
    )
    @GetMapping("/search")
    @Override
    public Map<String, Object> searchAccounts(@RequestParam Map<String, String> allParams) {
        return accountService.searchAccounts(allParams);
    }

    @Operation(summary = "Поиск аккаунта по статус-коду отношений в микросервисе Friends. Этот контроллер ссылается на глобальный поиск аккаунтов /search, так как в нем учтен statusCode.")
    @GetMapping("/search/statusCode")
    @Override
    public Map<String, Object> searchByStatusCode(@RequestParam Map<String, String> allParams) {
        return accountService.searchAccounts(allParams);
    }


    @Operation(summary = "Получение общего количества аккаунтов для telegram-бота")
    @GetMapping("/total")
    @Override
    public long getTotalAccountsCount() {
        return accountService.getTotalAccountsCount();
    }


    @Operation(summary = "Прием UUID от сервиса Dialogs через Webclient о завершении сессии вебсокета у аккаунта: как флаг перехода в статус offline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UUID успешно обработан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping("/lastAction/{uuid}")
    @Override
    public void receiveUUIDFromPath(@PathVariable String uuid) {
        accountService.processUUID(uuid);
    }
}


