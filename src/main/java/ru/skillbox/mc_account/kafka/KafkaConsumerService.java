package ru.skillbox.mc_account.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.skillbox.common.events.CommonEvent;
import ru.skillbox.common.events.RegUserEvent;
import ru.skillbox.common.events.UserEvent;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.entity.Role;
import ru.skillbox.mc_account.repository.AccountRepository;
import ru.skillbox.mc_account.web.mapper.AccountMapper;

import java.time.Instant;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final KafkaProducerService kafkaProducerService;


    @KafkaListener(topics = "registration_sending", groupId = "userRegistration")
    public void listenRegistrationEvents(CommonEvent<RegUserEvent> commonEvent) {
        try {
            RegUserEvent regUserEvent = commonEvent.getData();
            Account account = new Account();
            account.setId(regUserEvent.getId());
            account.setFirstName(regUserEvent.getFirstName());
            account.setLastName(regUserEvent.getLastName());
            account.setEmail(regUserEvent.getEmail());
            account.setPassword(regUserEvent.getPassword());
            account.setRole(Role.USER);
            account.setRegDate(Instant.now());
            account.setDeleted(false);
            account.setOnline(false);
            account.setBlocked(false);

            accountRepository.save(account);

            CommonEvent<UserEvent> userEvent = accountMapper.toCommonEvent(account);
            kafkaProducerService.sendUserEvent(userEvent);

        } catch (Exception e) {
        }
    }
}
