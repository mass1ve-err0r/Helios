package software.baig.helios.persistence.entities;

import jakarta.persistence.*;

import java.time.Instant;

import software.baig.helios.persistence.common.LogLevel;


@Entity
@Table(
        name = "application_log",
        indexes = {
                @Index(
                        name = "idx_application_log_logged_at",
                        columnList = "logged_at"
                ),
                @Index(
                        name = "idx_application_log_level",
                        columnList = "level"
                ),
                @Index(
                        name = "idx_application_log_logged_at_level",
                        columnList = "logged_at, level"
                )
        }
)
public class ApplicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "logged_at",
            nullable = false
    )
    private Instant loggedAt;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "level",
            nullable = false,
            length = 16
    )
    private LogLevel level;

    @Column(
            name = "logger_name",
            nullable = false,
            length = 512
    )
    private String loggerName;

    @Column(
            name = "thread_name",
            length = 255
    )
    private String threadName;

    @Column(
            name = "message",
            nullable = false,
            columnDefinition = "text"
    )
    private String message;

    @Column(
            name = "exception",
            columnDefinition = "text"
    )
    private String exception;

    protected ApplicationLog() {
    }

    public Long getId() {
        return id;
    }

    public Instant getLoggedAt() {
        return loggedAt;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }

}