package software.baig.helios.persistence.dto.mappers;

import org.springframework.context.annotation.Lazy;

import software.baig.helios.Mapper;
import software.baig.helios.persistence.dto.DtoMapper;
import software.baig.helios.persistence.dto.models.DeviceDto;
import software.baig.helios.persistence.entities.Device;


@Mapper
@Lazy(false)
public class DeviceDtoMapper implements DtoMapper<Device, DeviceDto> {

    @Override
    public Device toDatabaseEntity(DeviceDto dto) {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public DeviceDto toDto(Device entity) {
        return new DeviceDto()
                .setName(entity.getName())
                .setToken(entity.getToken());
    }

}
