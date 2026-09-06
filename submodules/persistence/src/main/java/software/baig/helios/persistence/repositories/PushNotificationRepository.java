package software.baig.helios.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import software.baig.helios.persistence.entities.PushNotification;


@Repository
public interface PushNotificationRepository extends JpaRepository<PushNotification, String> {

    // auto-impl.

}
