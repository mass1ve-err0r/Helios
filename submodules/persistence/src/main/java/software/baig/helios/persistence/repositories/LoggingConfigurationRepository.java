package software.baig.helios.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import software.baig.helios.persistence.entities.LoggingConfiguration;


public interface LoggingConfigurationRepository extends JpaRepository<LoggingConfiguration, String> {

    Optional<LoggingConfiguration> findByLoggerName(String loggerName);

}
