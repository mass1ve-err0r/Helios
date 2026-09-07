package software.baig.helios.api.models.requests;

import org.springframework.boot.logging.LogLevel;


public record CreateLoggingConfigurationRequest(
        String loggerName,
        LogLevel level
) { }
