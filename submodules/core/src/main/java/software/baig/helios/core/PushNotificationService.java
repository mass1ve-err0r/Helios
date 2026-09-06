package software.baig.helios.core;

import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.core.models.PushNotificationResult;


public interface PushNotificationService {

    PushNotificationResult send(SendPushNotificationRequest sendRequest, String persistedPushNotificationId);

}
