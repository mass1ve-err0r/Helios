package software.baig.helios;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;


/**
 * Indicates that an annotated class is a "Mapper" component.
 * <br>
 * This annotation serves as a specialization of {@link Component @Component},
 * analogous to {@link org.springframework.stereotype.Service @Service} and
 * {@link org.springframework.stereotype.Repository @Repository}.
 * <br>
 * Classes annotated with @Mapper are auto-detected via classpath scanning
 * and registered as Spring beans.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Mapper {

    /**
     * The suggested component name (mirrors @Component's value).
     * Defaults to an empty string, which triggers auto-naming.
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}
