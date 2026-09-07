package software.baig.helios.persistence.entities;

import jakarta.persistence.*;

import org.hibernate.annotations.UuidGenerator;

import org.springframework.boot.logging.LogLevel;


@Entity
@Table(name = "logging_configuration",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_logging_configuration_logger_name",
                        columnNames = "logger_name"
                )
        }
)
public class LoggingConfiguration {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "logger_name", nullable = false)
    private String loggerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false)
    private LogLevel level;


    public String getId() {
        return id;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public LoggingConfiguration setLoggerName(
            String loggerName) {

        this.loggerName = loggerName;
        return this;
    }

    public LogLevel getLevel() {
        return level;
    }

    public LoggingConfiguration setLevel(
            LogLevel level) {

        this.level = level;
        return this;
    }
}
