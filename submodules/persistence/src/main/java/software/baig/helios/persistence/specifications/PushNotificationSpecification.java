package software.baig.helios.persistence.specifications;

import java.time.Instant;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import software.baig.helios.persistence.common.PushNotificationState;
import software.baig.helios.persistence.entities.PushNotification;


public final class PushNotificationSpecification {


    private PushNotificationSpecification() { }

    public static Specification<PushNotification> filter(Instant from, Instant to, PushNotificationState state, String receiver, String topic) {

        return (root, query, builder) -> {

            Predicate predicate = builder.conjunction();

            if (from != null) {
                predicate = builder.and(
                        predicate,
                        builder.greaterThanOrEqualTo(root.get("createdAt"), from)
                );
            }

            if (to != null) {
                predicate = builder.and(
                        predicate,
                        builder.lessThanOrEqualTo(root.get("createdAt"), to)
                );
            }

            if (state != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(root.get("state"), state)
                );
            }

            if (receiver != null && !receiver.isBlank()) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("receiver")),
                                "%"
                                        + receiver
                                        .trim()
                                        .toLowerCase()
                                        + "%"
                        )
                );
            }

            if (topic != null && !topic.isBlank()) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("topic")),
                                "%"
                                        + topic
                                        .trim()
                                        .toLowerCase()
                                        + "%"
                        )
                );
            }

            return predicate;
        };
    }

}
