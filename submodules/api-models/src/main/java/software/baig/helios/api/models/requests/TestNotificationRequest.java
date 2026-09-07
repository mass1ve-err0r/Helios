package software.baig.helios.api.models.requests;


public record TestNotificationRequest(
        String title,
        String subtitle,
        String message
) { }