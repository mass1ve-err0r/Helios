package software.baig.helios.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Service;


@Service
public class LoggingSystemControlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSystemControlService.class);

    private final LoggingSystem loggingSystem;


    @Autowired
    public LoggingSystemControlService(LoggingSystem loggingSystem) {
        this.loggingSystem = loggingSystem;
    }

    /**
     * Dynamically sets the log level for a class or package.
     *
     * @param loggerName Fully qualified class name (e.g. "com.example.service.MyService") or package name
     * @param level The desired LogLevel (TRACE, DEBUG, INFO, WARN, ERROR, OFF, or null to reset)
     */
    public void setLogLevel(String loggerName, LogLevel level) {
        loggingSystem.setLogLevel(loggerName, level);
    }

}
