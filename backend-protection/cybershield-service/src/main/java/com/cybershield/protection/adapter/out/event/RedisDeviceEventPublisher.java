package com.cybershield.protection.adapter.out.event;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisDeviceEventPublisher implements DeviceEventPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Le nom du tuyau (Topic)
    private static final String CHANNEL = "device-events";

    public RedisDeviceEventPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishDeviceCreated(Device device) {
        try {
            // 1. On convertit l'objet device en texte JSON
            String jsonEvent = objectMapper.writeValueAsString(device);

            // 2. On poste le message dans Redis
            redisTemplate.convertAndSend(CHANNEL, jsonEvent);

            log.info("Événement publié dans Redis [{}]: {}", CHANNEL, device.getId());

        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la sérialisation JSON", e);
        }
    }
}