package software.baig.helios.core;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;

import software.baig.helios.conditionals.ConditionalOnServiceType;
import software.baig.helios.core.impl.ApplePushNotificationServiceImpl;
import software.baig.helios.core.impl.NoopPushService;
import software.baig.helios.persistence.services.DeviceService;
import software.baig.helios.persistence.services.PushNotificationPersistenceService;
import software.baig.helios.utilities.Constants;
import software.baig.helios.utilities.configuration.HeliosProperties;


@Configuration
public class PushNotificationServiceBeanConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(PushNotificationServiceBeanConfiguration.class);

    private final HeliosProperties properties;


    @Autowired
    public PushNotificationServiceBeanConfiguration(HeliosProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnServiceType("apple")
    public ApnsClient apnsClient() throws Exception {
        String host = properties.useProductionServer()
                ? ApnsClientBuilder.PRODUCTION_APNS_HOST
                : ApnsClientBuilder.DEVELOPMENT_APNS_HOST;

        try (InputStream in = getClass().getResourceAsStream("/apn/" + Constants.APN_KEY_FILENAME)) {
            ApnsSigningKey signingKey = ApnsSigningKey.loadFromInputStream(in, properties.apns().teamId(), properties.apns().keyId());

            LOG.info("Loaded AuthKey for APN, is production: {}", properties.useProductionServer());

            return new ApnsClientBuilder()
                    .setApnsServer(host)
                    .setSigningKey(signingKey)
                    .build();
        }
    }

    @Bean
    @Qualifier(PushNotificationServiceClassifier.APPLE)
    @ConditionalOnServiceType("apple")
    public PushNotificationService applePushNotificationService(
            DeviceService ds,
            PushNotificationPersistenceService pnps,
            ApnsClient apnsClient
    ) {
        LOG.info("Creating Apple APN PushNotificationService...");
        return new ApplePushNotificationServiceImpl(apnsClient, ds, pnps);
    }

    @Bean
    @Qualifier(PushNotificationServiceClassifier.FIREBASE)
    @ConditionalOnServiceType("firebase")
    public PushNotificationService firebasePushNotificationService(
            DeviceService ds,
            PushNotificationPersistenceService pnps
    ) {
        LOG.info("Initializing FIREBASE (FCM) push notification service, providing NOP since currently not implemented...");
        return new NoopPushService();
    }

    @Bean
    @ConditionalOnMissingBean(name = { "applePushNotificationService", "firebasePushNotificationService" })
    public PushNotificationService pushNotificationService() {
        LOG.info("Initializing NOP push notification service");
        return new NoopPushService();
    }

}
