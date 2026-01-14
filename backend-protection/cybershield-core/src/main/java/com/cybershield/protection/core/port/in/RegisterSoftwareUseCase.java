package com.cybershield.protection.core.port.in;

import com.cybershield.protection.core.domain.Software;
import java.util.List;
import java.util.UUID;

// Supprime l'import de SoftwareInclusionRequest ici !
public interface RegisterSoftwareUseCase {
    Software register(UUID deviceId, String name, String version, String publisher, boolean isRunning);

    List<Software> findByDeviceId(UUID deviceId);
    List<Software> findCriticalSoftware();
}