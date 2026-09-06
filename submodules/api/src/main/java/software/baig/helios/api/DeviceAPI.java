package software.baig.helios.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.baig.helios.api.models.requests.RegisterReceiverRequest;
import software.baig.helios.persistence.entities.Device;
import software.baig.helios.persistence.services.DeviceService;
import software.baig.helios.utilities.configuration.HeliosProperties;


@RestController
@RequestMapping("api/devices")
public class DeviceAPI {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceAPI.class);

    private final HeliosProperties serviceProperties;
    private final DeviceService deviceService;


    @Autowired
    public DeviceAPI(HeliosProperties serviceProperties, DeviceService deviceService) {
        this.serviceProperties = serviceProperties;
        this.deviceService = deviceService;
    }

    @PostMapping("register")
    public ResponseEntity<Void> registerDevice(@RequestBody RegisterReceiverRequest request) {
        LOG.info("Received request to register device: {}", request);

        if (serviceProperties.registrationSafetyToken().equals(request.registrationToken())) {
            Device device = deviceService.registerDevice(request);
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.badRequest().build();
    }

}
