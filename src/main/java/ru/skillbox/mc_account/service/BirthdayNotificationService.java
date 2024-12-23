package ru.skillbox.mc_account.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.common.events.NotificationDto;
import ru.skillbox.common.events.NotificationEvent;
import ru.skillbox.common.events.NotificationServiceType;
import ru.skillbox.common.events.NotificationType;
import ru.skillbox.mc_account.entity.Account;
import ru.skillbox.mc_account.kafka.KafkaProducerService;
import ru.skillbox.mc_account.repository.AccountRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BirthdayNotificationService {

    private final AccountRepository accountRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    public void sendBirthdayNotifications() {
        List<Account> birthdayAccounts = accountRepository.findByTodayBirthday();

        if (!birthdayAccounts.isEmpty()) {
            for (Account account : birthdayAccounts) {
                NotificationDto friendBirthdayNotification = new NotificationDto(
                        UUID.randomUUID(),
                        account.getId(),
                        "Happy Birthday, " + account.getFirstName() + "!",
                        NotificationType.FRIEND_BIRTHDAY,
                        Instant.now(),
                        null,
                        NotificationServiceType.ACCOUNT,
                        UUID.randomUUID(),
                        false
                );
                NotificationEvent friendBirthdayEvent = new NotificationEvent();
                friendBirthdayEvent.setEventType("NOTIFICATION_EVENT");
                friendBirthdayEvent.setNotificationData(friendBirthdayNotification);
                kafkaProducerService.sendBirthdayNotification(friendBirthdayEvent);

                NotificationDto userBirthdayNotification = new NotificationDto(
                        UUID.randomUUID(),
                        account.getId(),
                        "Dear " + account.getFirstName() + ", it's your special day!",
                        NotificationType.USER_BIRTHDAY,
                        Instant.now(),
                        account.getId(),
                        NotificationServiceType.ACCOUNT,
                        UUID.randomUUID(),
                        false
                );
                NotificationEvent userBirthdayEvent = new NotificationEvent();
                userBirthdayEvent.setEventType("NOTIFICATION_EVENT");
                userBirthdayEvent.setNotificationData(userBirthdayNotification);
                kafkaProducerService.sendBirthdayNotification(userBirthdayEvent);
            }
        }
    }
}
