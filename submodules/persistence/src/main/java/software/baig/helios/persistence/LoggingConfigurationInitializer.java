package software.baig.helios.persistence;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import software.baig.helios.persistence.services.LoggingConfigurationService;


@Component
public class LoggingConfigurationInitializer {

    private final LoggingConfigurationService configurationService;


    public LoggingConfigurationInitializer(LoggingConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void applyPersistedConfiguration() {
        configurationService.applyPersistedConfiguration();
    }

}
