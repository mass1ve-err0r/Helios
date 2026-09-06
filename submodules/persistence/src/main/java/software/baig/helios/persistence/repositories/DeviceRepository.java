package software.baig.helios.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import software.baig.helios.persistence.entities.Device;


@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    Optional<Device> findByToken(String token);
    Optional<Device> findByName(String name);

}
