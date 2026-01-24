package com.cybershield.protection.adapter.in.rest;

import com.cybershield.protection.core.port.out.event.DeviceEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test")
public class TestEventController {

    private final DeviceEventPublisher eventPublisher;

    public TestEventController(DeviceEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    // Endpoint de test pour déclencher une alerte de quarantaine
    @GetMapping("/alert")
    public Mono<String> triggerTestAlert() {
        UUID testId = UUID.randomUUID();
        // On appelle directement ton publisher pour tester le canal Redis
        eventPublisher.publishQuarantineAlert(
                testId,
                "TEST MANUEL : Détection d'intrusion simulée",
                99.0
        );
        return Mono.just("Alerte de test envoyée pour le device : " + testId);
    }

    @PostMapping("/publish-quarantine")
    public Mono<Void> testQuarantine() {
        return Mono.fromRunnable(() ->
                eventPublisher.publishQuarantineAlert(
                        UUID.randomUUID(),
                        "TEST MANUEL REDIS : Vérification du bus de données",
                        99.9
                )
        );
    }
}