package ru.skillbox.mc_account.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.skillbox.common.events.CommonEvent;
import ru.skillbox.common.events.DeleteFileEvent;
import ru.skillbox.common.events.UserEvent;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.exception.InvalidInputException;
import ru.skillbox.mc_account.exception.ResourceNotFoundException;
import ru.skillbox.mc_account.kafka.KafkaProducerService;
import ru.skillbox.mc_account.repository.AccountRepository;
import ru.skillbox.mc_account.security.AccountDetails;
import ru.skillbox.mc_account.service.impl.AccountServiceInterface;
import ru.skillbox.mc_account.web.DTO.*;
import ru.skillbox.mc_account.web.mapper.AccountMapper;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AccountService implements AccountServiceInterface {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    private final PasswordEncoder passwordEncoder;

    private final KafkaProducerService kafkaProducerService;

    private final RestTemplate restTemplate;


    private static final String FRIENDS_SERVICE_URL = "http://79.174.80.223:8085/api/v1/friends/check";
    private static final String FRIENDS_SERVICE_URL_2 = "http://79.174.80.223:8085/api/v1/friends/status";


    @Validated
    @Override
    public AccountMeDTO createAccount(@Valid AccountMeDTO accountMeDTO) {
        try {
            Account account = prepareAccount(accountMeDTO);
            Account savedAccount = accountRepository.save(account);
            sendUserCreatedEvent(savedAccount);
            return accountMapper.toDTO(savedAccount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при обработке запроса");
        }
    }

    private Account prepareAccount(AccountMeDTO accountMeDTO) {
        Account account = accountMapper.toEntity(accountMeDTO);
        account.setCreatedOn(Instant.now());
        account.setPassword(passwordEncoder.encode(accountMeDTO.getPassword()));
        return account;
    }

    private void sendUserCreatedEvent(Account account) {
        CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
        kafkaProducerService.sendUserEvent(userEvent);
    }

    @Override
    public AccountResponseDTO getAccount(String email) {
        validateEmail(email);
        return accountRepository.findByEmail(email)
                .map(accountMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email must not be empty");
        }
    }


    @Override
    public AccountMeDTO getAccountMe(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setOnline(true);
        account.setLastOnlineTime(Instant.now());
        accountRepository.save(account);

        return accountMapper.toDTO(account);
    }

    @Override
    public AccountMeDTO updateAccountMe(AccountUpdateDTO accountUpdateDTO) {
        String email = getCurrentUserEmail();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        accountMapper.updateAccountFromDTO(accountUpdateDTO, account);
        account.setUpdatedOn(Instant.now());
        accountRepository.save(account);

        sendUserUpdatedEvent(account);

        return accountMapper.toDTO(account);
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void sendUserUpdatedEvent(Account account) {
        CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
        kafkaProducerService.sendUserEvent(userEvent);
    }

    @Override
    public void markAccountAsDeleted(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setDeleted(true);
        account.setDeletionTimestamp(Instant.now());
        accountRepository.save(account);

        sendUserDeletedEvent(account);
    }

    private void sendUserDeletedEvent(Account account) {
        CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
        kafkaProducerService.sendUserEvent(userEvent);
    }

    @Override
    public AccountDataDTO getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found for this account. Please assign a role.");
        }

        UUID currentUserId = getUserIdFromContext();
        String authToken = getTokenFromContext();
        String statusCode = fetchStatusFromFriendsService(id, currentUserId, authToken);
        AccountDataDTO accountDataDTO = accountMapper.toAccountDetailsDTO(account);
        accountDataDTO.setStatusCode(statusCode);

        return accountDataDTO;
    }


    private String fetchStatusFromFriendsService(UUID accountId, UUID currentUserId, String authToken) {
        try {
            String urlWithParams = String.format("%s?ids=%s", FRIENDS_SERVICE_URL, currentUserId + "," + accountId);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    urlWithParams, HttpMethod.GET, entity, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch status from friends service");
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error communicating with friends service", ex);
        }
    }


    private String getTokenFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AccountDetails) {
            AccountDetails userDetails = (AccountDetails) principal;
            return userDetails.getToken();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: Token not found");
    }

    private UUID getUserIdFromContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AccountDetails) {
            return ((AccountDetails) principal).getId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized: User ID not found in security context");
    }

    @Override
    public void markAccountAsDeleted(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        account.setDeleted(true);
        account.setDeletionTimestamp(Instant.now());
        CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
        kafkaProducerService.sendUserEvent(userEvent);
        List<String> urls = new ArrayList<>();
        if (account.getPhoto() != null) {
            urls.add(account.getPhoto());
        }
        if (account.getProfileCover() != null) {
            urls.add(account.getProfileCover());
        }
        if (!urls.isEmpty()) {
            DeleteFileEvent deleteFileEvent = new DeleteFileEvent();
            deleteFileEvent.setUrlsImageForDelete(urls);
            kafkaProducerService.sendDeleteFileEvent(deleteFileEvent);
            account.setPhoto(null);
            account.setProfileCover(null);
        }
        accountRepository.save(account);
    }

    @Override
    public void markAccountAsBlocked(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        account.setBlocked(true);
        accountRepository.save(account);
        CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
        kafkaProducerService.sendUserEvent(userEvent);
    }

    @Override
    public Map<String, Object> searchAccounts(Map<String, String> allParams) {
        String author = null;
        String[] ids = null;
        int size = 10;
        boolean isDeleted;
        String firstName = null;
        String lastName = null;
        Integer ageFrom = null;
        Integer ageTo = null;
        String country = null;
        String city = null;
        String statusCode = null;

        // Обработка параметров
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.matches("\\d+")) {
                String[] parts = value.split("=", 2);
                if (parts.length == 2) {
                    switch (parts[0]) {
                        case "size" -> size = Integer.parseInt(parts[1]);
                        case "ids" -> ids = processIds(parts[1]);
                        case "firstName" -> firstName = parts[1];
                        case "lastName" -> lastName = parts[1];
                        case "ageFrom" -> ageFrom = Integer.parseInt(parts[1]);
                        case "ageTo" -> ageTo = Integer.parseInt(parts[1]);
                        case "country" -> country = parts[1];
                        case "city" -> city = parts[1];
                        case "author" -> author = parts[1];
                    }
                }
            } else {
                switch (key) {
                    case "ids" -> ids = processIds(value);
                    case "firstName" -> firstName = value;
                    case "lastName" -> lastName = value;
                    case "statusCode" -> statusCode = value;
                }
            }
        }

        if (ids != null) {
            ids = Arrays.stream(ids).distinct().toArray(String[]::new);
        }

        isDeleted = Boolean.parseBoolean(allParams.getOrDefault("isDeleted", "false"));

        boolean hasSearchParams = firstName != null || lastName != null || ageFrom != null || ageTo != null || country != null || city != null ||
                ids != null && ids.length > 0 || author != null || statusCode != null;

        if (!hasSearchParams) {
            return buildEmptyResponse(size);
        }

        List<UUID> friendUuids = new ArrayList<>();
        if (statusCode != null) {
            friendUuids = fetchFriendsList(statusCode);
        }

        List<SearchFriendsDTO> searchResults = searchAccountsByCriteria(
                author,
                ids != null ? ids : new String[0],
                isDeleted,
                firstName,
                lastName,
                ageFrom,
                ageTo,
                country,
                city,
                size,
                friendUuids
        );

        return buildSearchResultsResponse(searchResults, size);
    }

    private String[] processIds(String value) {
        if (value == null || value.isBlank()) {
            return new String[0];
        }
        return value.split(",");
    }

    private Map<String, Object> buildEmptyResponse(int size) {
        Map<String, Object> emptyResponse = new LinkedHashMap<>();
        emptyResponse.put("content", Collections.emptyList());
        emptyResponse.put("totalElements", 0);
        emptyResponse.put("totalPages", 0);
        emptyResponse.put("size", size);
        emptyResponse.put("number", 0);
        return emptyResponse;
    }

    private Map<String, Object> buildSearchResultsResponse(List<SearchFriendsDTO> searchResults, int size) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", searchResults);
        response.put("totalElements", searchResults.size());
        response.put("totalPages", (searchResults.size() + size - 1) / size);
        response.put("size", size);
        response.put("number", 0);
        response.put("showFriends", true);
        return response;
    }

    public List<SearchFriendsDTO> searchAccountsByCriteria(
            String author, String[] ids, boolean isDeleted,
            String firstName, String lastName, Integer ageFrom, Integer ageTo, String country, String city, int size,
            List<UUID> friendUuids
    ) {


        try {
            Specification<Account> spec = Specification.where(null);

            if (author != null) {
                spec = spec.and(AccountSpecifications.searchByKeywords(new String[]{author}));
            }

            if (firstName != null) {
                spec = spec.and(AccountSpecifications.byFirstName(firstName));
            }

            if (lastName != null) {
                spec = spec.and(AccountSpecifications.byLastName(lastName)); // Добавляем спецификацию для фамилии
            }

            if (ids != null && ids.length > 0) {
                UUID[] uuidArray = Arrays.stream(ids).map(UUID::fromString).toArray(UUID[]::new);
                spec = spec.and(AccountSpecifications.byIds(uuidArray));
            }

            if (!isDeleted) {
                spec = spec.and(AccountSpecifications.isDeleted(isDeleted));
            }

            if (ageFrom != null || ageTo != null) {
                spec = spec.and(AccountSpecifications.byAgeRange(ageFrom, ageTo));
            }

            if (country != null) {
                spec = spec.and(AccountSpecifications.byCountry(country));
            }

            if (country != null) {
                spec = spec.and(AccountSpecifications.byCity(city));
            }

            if (friendUuids != null && !friendUuids.isEmpty() && firstName != null) {
                spec = spec.and(AccountSpecifications.byFriendUuidsAndFirstName(friendUuids, firstName));
            }

            Pageable pageable = PageRequest.of(0, size);
            Page<Account> accountsPage = accountRepository.findAll(spec, pageable);

            return accountsPage.stream()
                    .map(accountMapper::toSearchFriendsDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw e;
        }
    }

    public List<UUID> fetchFriendsList(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Статус не может быть пустым или null");
        }
        try {
            String urlWithParams = String.format("%s/%s", FRIENDS_SERVICE_URL_2, status);
            String authToken = getTokenFromContext();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List<UUID>> response = restTemplate.exchange(
                    urlWithParams,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<UUID>>() {
                    }
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при получении списка друзей от сервиса FRIENDS");
            }
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка клиента при запросе к FRIENDS", ex);
        } catch (HttpServerErrorException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера FRIENDS", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Непредвиденная ошибка при запросе к FRIENDS", ex);
        }
    }


    @Override
    public long getTotalAccountsCount() {
        return accountRepository.count();
    }


    @Transactional
    @Override
    public void processUUID(String uuid) {
        try {
            UUID parsedUUID = UUID.fromString(uuid);
            Instant now = Instant.now();
            accountRepository.findById(parsedUUID).ifPresentOrElse(account -> {
                account.setOnline(false);
                account.setLastOnlineTime(now);
                accountRepository.save(account);
            }, () -> log.warn("Аккаунт с UUID {} не найден в базе", parsedUUID));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный UUID", e);
        }
    }
}