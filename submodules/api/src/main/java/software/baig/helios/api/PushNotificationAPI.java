package software.baig.helios.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.core.PushNotificationService;
import software.baig.helios.core.PushNotificationServiceClassifier;
import software.baig.helios.core.models.PushNotificationResult;
import software.baig.helios.persistence.services.PushNotificationPersistenceService;


@RestController
@RequestMapping("api/pushnotifications")
public class PushNotificationAPI {

    private static final Logger LOG = LoggerFactory.getLogger(PushNotificationAPI.class);

    private final PushNotificationService pushNotificationService;
    private final PushNotificationPersistenceService notificationPersistenceService;


    @Autowired
    public PushNotificationAPI(
            @Qualifier(PushNotificationServiceClassifier.APPLE) PushNotificationService pushNotificationService,
            PushNotificationPersistenceService pushNotificationPersistenceService) {
        this.pushNotificationService = pushNotificationService;
        this.notificationPersistenceService = pushNotificationPersistenceService;
    }

    @PostMapping("send")
    public ResponseEntity<PushNotificationResult> send(@RequestBody SendPushNotificationRequest sendRequest) {
        LOG.info("Received push notification request: {}", sendRequest);

        String persistedNotificationId = notificationPersistenceService.persist(sendRequest);
        PushNotificationResult result = pushNotificationService.send(sendRequest, persistedNotificationId);

        return ResponseEntity.ok(result);
    }

}
