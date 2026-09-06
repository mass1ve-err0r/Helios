package software.baig.helios.core.impl;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;

import software.baig.helios.BeanImplementation;
import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.core.PushNotificationService;
import software.baig.helios.core.models.PushNotificationResult;
import software.baig.helios.persistence.common.PushNotificationState;
import software.baig.helios.persistence.dto.models.DeviceDto;
import software.baig.helios.persistence.services.DeviceService;
import software.baig.helios.persistence.services.PushNotificationPersistenceService;


@BeanImplementation
public class ApplePushNotificationServiceImpl implements PushNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplePushNotificationServiceImpl.class);

    private enum Outcome { ACCEPTED, REJECTED, ERROR }

    private final ApnsClient client;
    private final DeviceService deviceService;
    private final PushNotificationPersistenceService notificationPersistenceService;


    public ApplePushNotificationServiceImpl(ApnsClient client, DeviceService deviceService, PushNotificationPersistenceService notificationPersistenceService) {
        this.client = client;
        this.deviceService = deviceService;
        this.notificationPersistenceService = notificationPersistenceService;
    }

    @Override
    public PushNotificationResult send(SendPushNotificationRequest sendRequest, String persistedPushNotificationId) {
        if ("ALL".equals(sendRequest.receiver())) {
            return broadcastNotification(sendRequest, persistedPushNotificationId);
        } else {
            return sendNotification(sendRequest, persistedPushNotificationId);
        }
    }

    private PushNotificationResult sendNotification(SendPushNotificationRequest sendRequest, String persistedNotificationId) {
        LOG.info("Sending single push notification to {}", sendRequest.receiver());
        DeviceDto device = deviceService.deviceByNameAsDto(sendRequest.receiver());

        if (device == null) {
            LOG.error("No registered device for receiver {}", sendRequest.receiver());
            return new PushNotificationResult(false, "No registered device for receiver " + sendRequest.receiver(), null);
        }

        String payload = buildPayload(sendRequest, persistedNotificationId);
        CompletableFuture<Outcome> sendFuture = dispatch(device, sendRequest.topic() , payload, persistedNotificationId);

        Outcome sendOutcome = sendFuture.join();
        if (sendOutcome != Outcome.ACCEPTED) {
            LOG.error("Failed to send push notification for Bundle-ID: {}", sendRequest.topic());
            return new PushNotificationResult(false, sendRequest.topic(), null);
        }

        return new PushNotificationResult(true, sendRequest.topic(), null);
    }

    private PushNotificationResult broadcastNotification(SendPushNotificationRequest sendRequest, String persistedNotificationId) {
        LOG.info("Sending broadcast push notification to {}", sendRequest.topic());
        List<DeviceDto> devices = deviceService.allAsDto();

        if (devices.isEmpty()) {
            LOG.error("No registered devices, nowhere to send!");
            return new PushNotificationResult(false, "No registered devices, nothing sent", null);
        }

        LOG.info("Sending broadcast push notification to {} devices", devices.size());

        String payload = buildPayload(sendRequest, persistedNotificationId);
        List<CompletableFuture<Outcome>> sends = devices.parallelStream()
                .map(device -> dispatch(device, sendRequest.topic(), payload, persistedNotificationId))
                .toList();

        Map<Outcome, Long> tally = sends.stream().collect(groupingBy(CompletableFuture::join, counting()));

        long accepted = tally.getOrDefault(Outcome.ACCEPTED, 0L);
        long rejected = tally.getOrDefault(Outcome.REJECTED, 0L);
        long errored = tally.getOrDefault(Outcome.ERROR, 0L);
        int total = sends.size();

        String message = String.format("APNs accepted %d/%d (rejected=%d, errored=%d)", accepted, total, rejected, errored);
        LOG.info("sendToAll: {}", message);

        boolean success = accepted == total;

        return new PushNotificationResult(success, message, null);
    }

    private CompletableFuture<Outcome> dispatch(DeviceDto device, String topic, String payload, String persistedNotificationId) {
        notificationPersistenceService.setPayload(persistedNotificationId, payload);
        notificationPersistenceService.updateState(persistedNotificationId, PushNotificationState.CREATED);

        LOG.info("Persisted initial state of push notification, attempting dispatch to {} for topic {}", device.getName(), topic);

        String token = TokenUtil.sanitizeTokenString(device.getToken());
        SimpleApnsPushNotification notification = new SimpleApnsPushNotification(token, topic, payload);

        return client.sendNotification(notification)
                .handle((PushNotificationResponse<SimpleApnsPushNotification> response, Throwable cause) -> {
                    if (cause != null) {
                        LOG.error("Send failed for device '{}': {}", device.getName(), cause.getMessage());
                        notificationPersistenceService.updateState(persistedNotificationId, PushNotificationState.DISPATCH_FAILED);

                        return Outcome.ERROR;
                    }
                    if (response.isAccepted()) {
                        LOG.info("Successfully send push notification for device '{}'", device.getName());
                        notificationPersistenceService.updateState(persistedNotificationId, PushNotificationState.DISPATCHED);
                        return Outcome.ACCEPTED;
                    }
                    LOG.error("Send rejected for device '{}': {}", device.getName(), response.getRejectionReason().orElse("unknown"));
                    notificationPersistenceService.updateState(persistedNotificationId, PushNotificationState.DISPATCH_FAILED);

                    return Outcome.REJECTED;
                });
    }

    private String buildPayload(SendPushNotificationRequest sendRequest, String persistedNotificationId) {
        SimpleApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
        payloadBuilder.setAlertTitle(sendRequest.title());
        payloadBuilder.setAlertSubtitle(sendRequest.subtitle());
        payloadBuilder.setAlertBody(sendRequest.message());

        // Helios specific
        payloadBuilder.setMutableContent(true);
        payloadBuilder.addCustomProperty("id", persistedNotificationId);

        return payloadBuilder.build();
    }

}
