package software.baig.helios.core.models;


public record PushNotificationResult(
        boolean success,
        String message,
        Throwable error
) {

    public static final PushNotificationResult SUCCESS = new PushNotificationResult(true, null, null);


    public static PushNotificationResult failure(Throwable error) {
        return new PushNotificationResult(false, error.getMessage(), error);
    }

}
