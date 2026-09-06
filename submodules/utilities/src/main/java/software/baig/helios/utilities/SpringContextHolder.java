package software.baig.helios.utilities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public final class SpringContextHolder implements ApplicationContextAware {

    private static volatile ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> type) {
        ApplicationContext context = applicationContext;

        if (context == null) {
            return null;
        }

        return context.getBean(type);
    }

}