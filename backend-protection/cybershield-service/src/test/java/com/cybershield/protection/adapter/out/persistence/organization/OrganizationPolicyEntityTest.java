package com.cybershield.protection.adapter.out.persistence.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// On utilise la vraie base de données configurée pour les tests
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrganizationPolicyEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void should_persist_and_retrieve_entity_correctly() {
        // 1. GIVEN : On prépare l'entité avec des données
        UUID id = UUID.randomUUID();
        // On simule une longue liste de MAC addresses
        String macAddressList = "AA:BB:CC:DD:EE:FF,11:22:33:44:55:66,99:88:77:66:55:44";

        OrganizationPolicyEntity entityToSave = new OrganizationPolicyEntity(
                id,
                "Test Corp Entity",
                100,
                50,
                2,
                LocalTime.of(9, 0),  // Start
                LocalTime.of(18, 0), // End
                macAddressList
        );

        // 2. WHEN : On persiste (INSERT)
        // persistAndFlush pour s'assurer que l'INSERT est effectué immédiatement
        OrganizationPolicyEntity savedEntity = entityManager.persistAndFlush(entityToSave);

        // On vide le cache d'Hibernate pour forcer un SELECT lors de la relecture
        entityManager.clear();

        // 3. THEN : On relit (SELECT) et on vérifie
        OrganizationPolicyEntity foundEntity = entityManager.find(OrganizationPolicyEntity.class, id);

        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getId()).isEqualTo(id);
        assertThat(foundEntity.getCompanyName()).isEqualTo("Test Corp Entity");
        assertThat(foundEntity.getTotalEmployees()).isEqualTo(100);

        // Vérification des types temporels
        assertThat(foundEntity.getWorkDayStart()).isEqualTo(LocalTime.of(9, 0));

        // Vérification que la longue chaîne de MAC addresses est bien persistée et relue
        assertThat(foundEntity.getCriticalMacAddresses()).isEqualTo(macAddressList);
    }
}