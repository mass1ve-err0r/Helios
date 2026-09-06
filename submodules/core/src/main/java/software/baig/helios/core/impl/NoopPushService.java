package software.baig.helios.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.core.PushNotificationService;
import software.baig.helios.core.models.PushNotificationResult;


public class NoopPushService implements PushNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NoopPushService.class);


    @Override
    public PushNotificationResult send(SendPushNotificationRequest sendRequest, String persistedPushNotificationId) {
        LOG.info("NOP PushNotificationService triggered, returning true...");
        return PushNotificationResult.SUCCESS;
    }

}
