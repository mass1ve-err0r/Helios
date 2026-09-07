package software.baig.helios.utilities.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "helios")
public record HeliosProperties(
        Apns apns,
        boolean useProductionServer,
        String registrationSafetyToken,
        List<String> serviceType
) {
    public record Apns(String teamId, String keyId, String bundleId) { }
    public record Fcm(String id) { }
}
