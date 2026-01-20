package com.cybershield.protection.adapter.in.rest.dto.global;

import com.cybershield.protection.adapter.in.rest.dto.device.DeviceEnrollmentRequest;
import com.cybershield.protection.adapter.in.rest.dto.network.NetworkTrafficRequest;
import com.cybershield.protection.adapter.in.rest.dto.software.SoftwareInclusionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GlobalEnrollmentRequest(
        @NotNull @Valid DeviceEnrollmentRequest device,
        @NotEmpty @Valid List<SoftwareInclusionRequest> softwares,
        @NotNull @Valid NetworkTrafficRequest initialTraffic
) {}