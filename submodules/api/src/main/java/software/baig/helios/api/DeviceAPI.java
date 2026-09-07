package software.baig.helios.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.baig.helios.api.models.requests.RegisterReceiverRequest;
import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.api.models.requests.TestNotificationRequest;
import software.baig.helios.core.PushNotificationService;
import software.baig.helios.core.PushNotificationServiceClassifier;
import software.baig.helios.core.models.PushNotificationResult;
import software.baig.helios.persistence.dto.models.DeviceOverviewDto;
import software.baig.helios.persistence.services.DeviceService;
import software.baig.helios.persistence.services.PushNotificationPersistenceService;
import software.baig.helios.utilities.configuration.HeliosProperties;


@RestController
@RequestMapping("api/devices")
public class DeviceAPI {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceAPI.class);

    private final HeliosProperties serviceProperties;
    private final DeviceService deviceService;
    private final PushNotificationService pushNotificationService;
    private final PushNotificationPersistenceService notificationPersistenceService;
    private final HeliosProperties heliosProperties;


    @Autowired
    public DeviceAPI(
            HeliosProperties serviceProperties,
            DeviceService deviceService,
            @Qualifier(PushNotificationServiceClassifier.APPLE) PushNotificationService pushNotificationService,
            PushNotificationPersistenceService notificationPersistenceService, HeliosProperties heliosProperties) {
        this.serviceProperties = serviceProperties;
        this.deviceService = deviceService;
        this.pushNotificationService = pushNotificationService;
        this.notificationPersistenceService = notificationPersistenceService;
        this.heliosProperties = heliosProperties;
    }

    @GetMapping
    public List<DeviceOverviewDto> getDevices() {
        return deviceService.allAsOverviewDto();
    }

    @PostMapping("register")
    public ResponseEntity<Void> registerDevice(@RequestBody RegisterReceiverRequest request) {
        LOG.info("Received request to register device: {}", request);

        if (serviceProperties.registrationSafetyToken().equals(request.registrationToken())) {
            deviceService.registerDevice(request);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("{id}/test-notification")
    public ResponseEntity<PushNotificationResult> sendTestNotification(@PathVariable String id, @RequestBody TestNotificationRequest request) {
        LOG.info("Received request to send test notification: {}", request);

        SendPushNotificationRequest sendRequest = new SendPushNotificationRequest(
                heliosProperties.apns().bundleId(),
                id,
                request.title(),
                request.subtitle(),
                request.message()
        );

        String persistedNotificationId = notificationPersistenceService.persist(sendRequest);
        PushNotificationResult result = pushNotificationService.send(sendRequest, persistedNotificationId);

        return ResponseEntity.ok(result);
    }

}
