package software.baig.helios.persistence.dto.models;

import java.time.Instant;


public record DeviceOverviewDto(
        String id,
        Instant createdAt,
        Instant lastSeen,
        String name
) { }