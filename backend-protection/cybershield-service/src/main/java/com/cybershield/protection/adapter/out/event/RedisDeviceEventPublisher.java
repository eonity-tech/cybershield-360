package com.cybershield.protection.adapter.out.event;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RedisDeviceEventPublisher implements DeviceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RedisDeviceEventPublisher.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL_EVENTS = "device-events";
    private static final String CHANNEL_ALERTS = "security-alerts"; // Nouveau canal pour les alertes

    // Constructeur avec injection des dépendances
    public RedisDeviceEventPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Publier un événement de création de device
    @Override
    public void publishDeviceCreated(Device device) {
        publishToChannel(CHANNEL_EVENTS, device, device.getId());
    }

    // Publier une alerte de quarantaine
    @Override
    public void publishQuarantineAlert(UUID deviceId, String reason, double riskScore) {
        Map<String, Object> alert = Map.of(
                "deviceId", deviceId,
                "action", "QUARANTINE",
                "reason", reason,
                "riskScore", riskScore,
                "timestamp", System.currentTimeMillis()
        );

        publishToChannel(CHANNEL_ALERTS, alert, deviceId);
    }

    // Méthode utilitaire pour publier sur un canal Redis
    private void publishToChannel(String channel, Object payload, UUID deviceId) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend(channel, jsonEvent);
            log.info("Événement [{}] envoyé pour le device: {}", channel, deviceId);
        } catch (JsonProcessingException e) {
            log.error("Erreur de sérialisation pour le canal " + channel, e);
        }
    }
}