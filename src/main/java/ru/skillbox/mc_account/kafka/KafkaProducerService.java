package ru.skillbox.mc_account.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.common.events.CommonEvent;
import ru.skillbox.common.events.DeleteFileEvent;
import ru.skillbox.common.events.NotificationEvent;
import ru.skillbox.common.events.UserEvent;

@Service
@AllArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, CommonEvent<UserEvent>> kafkaTemplate;
    private static final String TOPIC = "user-events";
    private static final String DELETE_FILE_EVENTS_TOPIC = "delete-file-events";
    private static final String NOTIFICATION_TOPIC = "common-notifications";

    private final KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate;

    private final KafkaTemplate<String, DeleteFileEvent> deleteFileKafkaTemplate;

    public void sendUserEvent(CommonEvent<UserEvent> commonEvent) {
        kafkaTemplate.send(TOPIC, commonEvent.getEventType(), commonEvent);
    }
    public void sendBirthdayNotification(NotificationEvent notificationEvent) {
        notificationKafkaTemplate.send(NOTIFICATION_TOPIC, notificationEvent.getEventType(), notificationEvent);
    }
    public void sendDeleteFileEvent(DeleteFileEvent deleteFileEvent) {
        deleteFileKafkaTemplate.send(DELETE_FILE_EVENTS_TOPIC, deleteFileEvent.getEventType(), deleteFileEvent);
    }
}


