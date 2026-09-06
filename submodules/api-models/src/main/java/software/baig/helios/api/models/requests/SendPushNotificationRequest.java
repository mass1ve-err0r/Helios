package software.baig.helios.api.models.requests;


public record SendPushNotificationRequest(
        String topic,
        String receiver,
        String title,
        String subtitle,
        String message
) { }
