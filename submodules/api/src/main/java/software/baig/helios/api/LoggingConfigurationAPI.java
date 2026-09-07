package software.baig.helios.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import software.baig.helios.api.models.requests.CreateLoggingConfigurationRequest;
import software.baig.helios.api.models.requests.UpdateLoggingConfigurationRequest;
import software.baig.helios.persistence.entities.LoggingConfiguration;
import software.baig.helios.persistence.services.LoggingConfigurationService;


@RestController
@RequestMapping("api/settings/logging")
public class LoggingConfigurationAPI {

    private final LoggingConfigurationService configurationService;


    @Autowired
    public LoggingConfigurationAPI(LoggingConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public List<LoggingConfiguration> getConfigurations() {
        return configurationService.all();
    }

    @PostMapping
    public ResponseEntity<LoggingConfiguration> createConfiguration(@RequestBody CreateLoggingConfigurationRequest request) {
        LoggingConfiguration configuration = configurationService.create(request.loggerName(), request.level());

        return ResponseEntity.ok(configuration);
    }

    @PutMapping("{id}")
    public ResponseEntity<LoggingConfiguration> updateConfiguration(@PathVariable String id, @RequestBody UpdateLoggingConfigurationRequest request) {
        LoggingConfiguration configuration = configurationService.update(id, request.level());

        return ResponseEntity.ok(configuration);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable String id) {
        configurationService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
