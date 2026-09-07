package software.baig.helios.api.models.requests;

import org.springframework.boot.logging.LogLevel;


public record UpdateLoggingConfigurationRequest(
        LogLevel level
) { }
