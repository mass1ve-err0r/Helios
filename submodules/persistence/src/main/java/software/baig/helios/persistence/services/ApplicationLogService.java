package software.baig.helios.persistence.services;

import static software.baig.helios.persistence.specifications.ApplicationLogSpecifications.*;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.baig.helios.persistence.entities.ApplicationLog;
import software.baig.helios.persistence.common.LogLevel;
import software.baig.helios.persistence.repositories.ApplicationLogRepository;


@Service
@Transactional(readOnly = true)
public class ApplicationLogService {

    private final ApplicationLogRepository repository;


    @Autowired
    public ApplicationLogService(ApplicationLogRepository repository) {
        this.repository = repository;
    }

    public Page<ApplicationLog> findLogs(
            Instant from,
            Instant to,
            LogLevel level,
            Pageable pageable
    ) {
        Specification<ApplicationLog> specification =
                Specification
                        .where(loggedAtFrom(from))
                        .and(loggedAtTo(to))
                        .and(level(level));

        return repository.findAll(
                specification,
                pageable
        );
    }

}
