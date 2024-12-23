package ru.skillbox.mc_account.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import ru.skillbox.common.events.CommonEvent;
import ru.skillbox.common.events.RegUserEvent;
import ru.skillbox.common.events.UserEvent;

import java.io.IOException;
import java.util.Map;

public class CommonEventDeserializer implements Deserializer<CommonEvent<?>> {

    private final ObjectMapper objectMapper;

    public CommonEventDeserializer() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public CommonEvent<?> deserialize(String topic, byte[] data) {
        try {
            if (topic.equals("user-events")) {
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(CommonEvent.class, UserEvent.class);
                return objectMapper.readValue(data, javaType);
            } else if (topic.equals("registration_sending")) {
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(CommonEvent.class, RegUserEvent.class);
                return objectMapper.readValue(data, javaType);
            } else {
                throw new IllegalArgumentException("Unknown topic: " + topic);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize CommonEvent", e);
        }
    }

    @Override
    public void close() {
    }
}
