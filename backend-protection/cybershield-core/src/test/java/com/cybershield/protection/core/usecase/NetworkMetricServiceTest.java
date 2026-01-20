package com.cybershield.protection.core.usecase;

import com.cybershield.protection.core.domain.Device;
import com.cybershield.protection.core.domain.NetworkMetric;
import com.cybershield.protection.core.domain.Software;
import com.cybershield.protection.core.domain.type.DeviceType;
import com.cybershield.protection.core.domain.type.OsType;
import com.cybershield.protection.core.model.GlobalDeviceDashboardSummary;
import com.cybershield.protection.core.port.out.DeviceRepository;
import com.cybershield.protection.core.port.out.NetworkRepository;
import com.cybershield.protection.core.port.out.SoftwareRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetworkMetricServiceTest {

    @Mock
    private NetworkRepository networkRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private SoftwareRepository softwareRepository;

    @InjectMocks
    private NetworkMetricService networkMetricService;

    // --- TEST 1 : Enregistrement du trafic ---

    @Test
    void shouldRecordMetricSuccessfully() {
        UUID deviceId = UUID.randomUUID();
        networkMetricService.record(deviceId, 100.0, 200.0);
        verify(networkRepository, times(1)).save(any(NetworkMetric.class));
    }

    @Test
    void shouldThrowExceptionWhenDeviceIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            networkMetricService.record(null, 100, 100);
        });
        verify(networkRepository, never()).save(any());
    }

    // --- TEST 2 : Le Dashboard ---

    @Test
    void shouldGenerateDashboardWithAlerts() {
        // 1. GIVEN
        UUID deviceId = UUID.randomUUID();
        Device mockDevice = new Device(
                deviceId, "00:11:22:33:44:55", "192.168.1.10",
                DeviceType.COMPUTER, OsType.WINDOWS, "10", "PC-Compta", "Dell", 128, ""
        );

        // Trafic > 500Mo
        NetworkMetric highTrafficMetric = new NetworkMetric(
                UUID.randomUUID(), deviceId, 300_000_000, 300_000_000
        );

        Software mockSoftware = new Software(
                UUID.randomUUID(), deviceId, "Chrome", "102.0", "Google", true
        );

        // 2. Mocking
        when(deviceRepository.findAll()).thenReturn(List.of(mockDevice));
        when(networkRepository.findLatestByDeviceId(deviceId)).thenReturn(highTrafficMetric);
        when(softwareRepository.findByDeviceId(deviceId)).thenReturn(List.of(mockSoftware));

        // 3. WHEN
        List<GlobalDeviceDashboardSummary> dashboard = networkMetricService.getGlobalDashboard();

        // 4. THEN
        assertNotNull(dashboard);
        assertEquals(1, dashboard.size());

        GlobalDeviceDashboardSummary summary = dashboard.get(0);

        // --- CORRECTIONS ICI : On utilise les noms exacts de ton Record ---
        assertEquals("PC-Compta", summary.hostname());
        assertEquals("192.168.1.10", summary.ipAddress());

        // Au lieu de totalTrafficBytes(), on utilise currentUsage()
        assertEquals(600_000_000, summary.currentUsage());

        // On vérifie le statusCode
        assertEquals(1, summary.statusCode(), "Le code doit être 1 (Critique)");

        // Au lieu de statusMessage(), on utilise message()
        assertTrue(summary.message().contains("ALERTE"), "Le message doit contenir ALERTE");

        // Au lieu de installedSoftware(), on utilise detectedSoftwares()
        assertTrue(summary.detectedSoftwares().contains("Chrome (v102.0)"));
    }

    @Test
    void shouldHandleDeviceWithNoTraffic() {
        UUID deviceId = UUID.randomUUID();
        Device newDevice = new Device(
                deviceId, "AA:BB:CC:DD:EE:FF", "10.0.0.1",
                DeviceType.IOT, OsType.UNKNOWN, "v1", "New-Cam", "Sony", 64, ""
        );

        when(deviceRepository.findAll()).thenReturn(List.of(newDevice));
        when(networkRepository.findLatestByDeviceId(deviceId)).thenReturn(null);
        when(softwareRepository.findByDeviceId(deviceId)).thenReturn(List.of());

        List<GlobalDeviceDashboardSummary> dashboard = networkMetricService.getGlobalDashboard();

        GlobalDeviceDashboardSummary summary = dashboard.get(0);

        assertEquals(0.0, summary.currentUsage());
        assertEquals(3, summary.statusCode());
    }
}