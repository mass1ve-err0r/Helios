package software.baig.helios.persistence.services;

import java.time.Instant;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.persistence.common.PushNotificationState;
import software.baig.helios.persistence.entities.PushNotification;
import software.baig.helios.persistence.repositories.PushNotificationRepository;


@Service
@Transactional(readOnly = true)
public class PushNotificationPersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PushNotificationPersistenceService.class);

    private final PushNotificationRepository repository;


    @Autowired
    public PushNotificationPersistenceService(PushNotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PushNotification save(SendPushNotificationRequest sendPushNotificationRequest) {
        PushNotification pn = new PushNotification();
        pn.setCreatedAt(Instant.now());
        pn.setTopic(sendPushNotificationRequest.topic());
        pn.setTitle(sendPushNotificationRequest.title());
        pn.setSubtitle(sendPushNotificationRequest.subtitle());
        pn.setBody(sendPushNotificationRequest.message());

        return repository.save(pn);
    }

    @Transactional
    public String persist(SendPushNotificationRequest sendPushNotificationRequest) {
        PushNotification pn = new PushNotification();
        pn.setCreatedAt(Instant.now());
        pn.setTopic(sendPushNotificationRequest.topic());
        pn.setTitle(sendPushNotificationRequest.title());
        pn.setSubtitle(sendPushNotificationRequest.subtitle());
        pn.setBody(sendPushNotificationRequest.message());
        pn.setReceiver(sendPushNotificationRequest.receiver());

        PushNotification persistedNotification = repository.save(pn);
        return persistedNotification.getId();
    }

    @Transactional
    public void setPayload(String pushNotificationId, String payload) {
        PushNotification target = getOrThrow(pushNotificationId);
        target.setPayload(payload);

        repository.save(target);
    }

    @Transactional
    public void updateState(String pushNotificationId, PushNotificationState newState) {
        PushNotification target = getOrThrow(pushNotificationId);
        target.setState(newState);

        repository.save(target);
    }


    private PushNotification getOrThrow(String id) throws EntityNotFoundException {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }

}
