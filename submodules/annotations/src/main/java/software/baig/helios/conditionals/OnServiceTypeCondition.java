package software.baig.helios.conditionals;

import java.util.List;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import software.baig.helios.utilities.Constants;


public class OnServiceTypeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String required = ((String) metadata
                .getAnnotationAttributes(ConditionalOnServiceType.class.getName())
                .get("value"))
                .trim();

        List<String> configured = Binder.get(context.getEnvironment())
                .bind(Constants.BP_SERVICE_TYPE, Bindable.listOf(String.class))
                .orElse(List.of());

        return configured.stream().anyMatch(value -> value.trim().equalsIgnoreCase(required));
    }

}
