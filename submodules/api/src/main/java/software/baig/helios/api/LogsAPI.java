package software.baig.helios.api;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import software.baig.helios.persistence.entities.ApplicationLog;
import software.baig.helios.persistence.common.LogLevel;
import software.baig.helios.persistence.services.ApplicationLogService;


@RestController
@RequestMapping("/api/logs")
public class LogsAPI {

    private final ApplicationLogService service;


    @Autowired
    public LogsAPI(ApplicationLogService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ApplicationLog> getLogs(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to,

            @RequestParam(required = false)
            LogLevel level,

            Pageable pageable
    ) {
        return service.findLogs(
                from,
                to,
                level,
                pageable
        );
    }

}
