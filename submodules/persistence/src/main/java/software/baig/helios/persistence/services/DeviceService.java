package software.baig.helios.persistence.services;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.baig.helios.api.models.requests.RegisterReceiverRequest;
import software.baig.helios.persistence.dto.mappers.DeviceDtoMapper;
import software.baig.helios.persistence.dto.models.DeviceDto;
import software.baig.helios.persistence.entities.Device;
import software.baig.helios.persistence.repositories.DeviceRepository;


@Service
public class DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository repository;
    private final DeviceDtoMapper dtoMapper;

    @Autowired
    public DeviceService(DeviceRepository repository, DeviceDtoMapper dtoMapper) {
        this.repository = repository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Device registerDevice(RegisterReceiverRequest registerRequest) {
        Device device = new Device();
        device.setCreatedAt(Instant.now());
        device.setLastSeen(Instant.now());
        device.setName(registerRequest.name());
        device.setToken(registerRequest.token());

        return  repository.save(device);
    }

    @Transactional(readOnly = true)
    public DeviceDto deviceByNameAsDto(String name) {
        return repository.findByName(name)
                .map(dtoMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public DeviceDto deviceByTokenAsDto(String token) {
        return repository.findByToken(token)
                .map(dtoMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> allAsDto() {
        return repository.findAll()
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<Device> all() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Device deviceByName(String name) {
        return repository.findByName(name).orElse(null);
    }

    @Transactional(readOnly = true)
    public Device deviceByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

}
