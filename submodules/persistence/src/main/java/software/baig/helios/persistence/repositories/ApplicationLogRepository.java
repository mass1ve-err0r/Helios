package software.baig.helios.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import software.baig.helios.persistence.entities.ApplicationLog;

public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, Long>, JpaSpecificationExecutor<ApplicationLog> {

    // auto-impl.

}