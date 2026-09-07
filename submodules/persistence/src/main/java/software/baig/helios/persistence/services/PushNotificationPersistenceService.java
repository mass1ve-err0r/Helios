package software.baig.helios.persistence.services;

import java.time.Instant;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.baig.helios.api.models.requests.SendPushNotificationRequest;
import software.baig.helios.persistence.common.PushNotificationState;
import software.baig.helios.persistence.entities.PushNotification;
import software.baig.helios.persistence.repositories.PushNotificationRepository;
import software.baig.helios.persistence.specifications.PushNotificationSpecification;


@Service
@Transactional(readOnly = true)
public class PushNotificationPersistenceService {

    private static final Logger LOG = LoggerFactory.getLogger(PushNotificationPersistenceService.class);

    private final PushNotificationRepository repository;


    @Autowired
    public PushNotificationPersistenceService(PushNotificationRepository repository) {
        this.repository = repository;
    }

    public Page<PushNotification> findNotifications(Instant from, Instant to, PushNotificationState state, String receiver, String topic, Pageable pageable) {
        return repository.findAll(
                PushNotificationSpecification.filter(
                        from,
                        to,
                        state,
                        receiver,
                        topic
                ),
                pageable
        );
    }

    @Transactional
    public PushNotification save(SendPushNotificationRequest request) {
        PushNotification notification = new PushNotification();
        notification.setCreatedAt(Instant.now());
        notification.setTopic(request.topic());
        notification.setTitle(request.title());
        notification.setSubtitle(request.subtitle());
        notification.setBody(request.message());

        return repository.save(notification);
    }

    @Transactional
    public String persist(SendPushNotificationRequest request) {
        PushNotification notification = new PushNotification();
        notification.setCreatedAt(Instant.now());
        notification.setTopic(request.topic());
        notification.setTitle(request.title());
        notification.setSubtitle(request.subtitle());
        notification.setBody(request.message());
        notification.setReceiver(request.receiver());

        PushNotification persistedNotification = repository.save(notification);

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
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

}
