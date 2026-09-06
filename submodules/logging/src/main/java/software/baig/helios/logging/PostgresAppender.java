package software.baig.helios.logging;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;

import software.baig.helios.utilities.SpringContextHolder;


public final class PostgresAppender extends AppenderBase<ILoggingEvent> {

    private static final String INSERT_SQL = """
            INSERT INTO application_log (
                logged_at,
                level,
                logger_name,
                thread_name,
                message,
                exception
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private volatile DataSource dataSource;

    @Override
    protected void append(ILoggingEvent event) {
        DataSource currentDataSource = resolveDataSource();

        if (currentDataSource == null) {
            return;
        }

        try (
                Connection connection = currentDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_SQL)
        ) {
            statement.setTimestamp(
                    1,
                    new Timestamp(event.getTimeStamp())
            );

            statement.setString(
                    2,
                    event.getLevel().toString()
            );

            statement.setString(
                    3,
                    event.getLoggerName()
            );

            statement.setString(
                    4,
                    event.getThreadName()
            );

            statement.setString(
                    5,
                    event.getFormattedMessage()
            );

            statement.setString(
                    6,
                    event.getThrowableProxy() == null
                            ? null
                            : ThrowableProxyUtil.asString(event.getThrowableProxy())
            );

            statement.executeUpdate();
        } catch (SQLException exception) {
            addError("Failed to persist logging event to PostgreSQL", exception);
        }
    }

    private DataSource resolveDataSource() {
        DataSource current = dataSource;

        if (current != null) {
            return current;
        }

        current = SpringContextHolder.getBean(DataSource.class);

        if (current != null) {
            dataSource = current;
        }

        return current;
    }

}
