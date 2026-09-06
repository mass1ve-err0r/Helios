package software.baig.helios.persistence.specifications;

import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;

import software.baig.helios.persistence.entities.ApplicationLog;
import software.baig.helios.persistence.common.LogLevel;


public final class ApplicationLogSpecifications {


    private ApplicationLogSpecifications() { }

    public static Specification<ApplicationLog> loggedAtFrom(Instant from) {
        return (root, query, builder) -> {
            if (from == null) {
                return builder.conjunction();
            }

            return builder.greaterThanOrEqualTo(
                    root.get("loggedAt"),
                    from
            );
        };
    }

    public static Specification<ApplicationLog> loggedAtTo(Instant to) {
        return (root, query, builder) -> {
            if (to == null) {
                return builder.conjunction();
            }

            return builder.lessThanOrEqualTo(
                    root.get("loggedAt"),
                    to
            );
        };
    }

    public static Specification<ApplicationLog> level(LogLevel level) {
        return (root, query, builder) -> {
            if (level == null) {
                return builder.conjunction();
            }

            return builder.equal(
                    root.get("level"),
                    level
            );
        };
    }

}
