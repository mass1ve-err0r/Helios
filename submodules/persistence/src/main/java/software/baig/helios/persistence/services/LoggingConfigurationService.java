package software.baig.helios.persistence.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.baig.helios.logging.LoggingSystemControlService;
import software.baig.helios.persistence.entities.LoggingConfiguration;
import software.baig.helios.persistence.repositories.LoggingConfigurationRepository;


@Service
public class LoggingConfigurationService {

    private final LoggingConfigurationRepository repository;
    private final LoggingSystemControlService loggingSystemControlService;


    @Autowired
    public LoggingConfigurationService(LoggingConfigurationRepository repository, LoggingSystemControlService loggingSystemControlService) {
        this.repository = repository;
        this.loggingSystemControlService = loggingSystemControlService;
    }

    @Transactional(readOnly = true)
    public List<LoggingConfiguration> all() {
        return repository.findAll();
    }

    @Transactional
    public LoggingConfiguration create(String loggerName, LogLevel level) {
        LoggingConfiguration configuration = repository
                .findByLoggerName(loggerName)
                .orElseGet(LoggingConfiguration::new);

        configuration
                .setLoggerName(loggerName)
                .setLevel(level);

        LoggingConfiguration saved = repository.save(configuration);

        loggingSystemControlService.setLogLevel(loggerName, level);

        return saved;
    }

    @Transactional
    public LoggingConfiguration update(String id, LogLevel level) {
        LoggingConfiguration configuration = repository.findById(id).orElseThrow();
        configuration.setLevel(level);

        LoggingConfiguration saved = repository.save(configuration);

        loggingSystemControlService.setLogLevel(configuration.getLoggerName(), level);

        return saved;
    }

    @Transactional
    public void delete(String id) {
        LoggingConfiguration configuration = repository.findById(id).orElseThrow();

        repository.delete(configuration);

        loggingSystemControlService.setLogLevel(configuration.getLoggerName(), null);
    }

    @Transactional(readOnly = true)
    public void applyPersistedConfiguration() {
        repository.findAll()
                .forEach(configuration ->
                        loggingSystemControlService.setLogLevel(
                                configuration.getLoggerName(),
                                configuration.getLevel()
                        )
                );
    }

}
