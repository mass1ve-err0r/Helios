package software.baig.helios.persistence.common;


public enum PushNotificationState {

    /* PushNotification created & persisted */
    CREATED,

    /* PushNotification successfully sent */
    DISPATCHED,

    /* PushNotification was not successfully sent */
    DISPATCH_FAILED
    ;

}
