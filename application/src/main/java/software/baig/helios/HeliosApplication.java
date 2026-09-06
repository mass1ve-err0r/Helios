package software.baig.helios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import software.baig.helios.utilities.configuration.HeliosProperties;


@EnableConfigurationProperties(HeliosProperties.class)
@SpringBootApplication
public class HeliosApplication {

    static void main() {
        SpringApplication.run(HeliosApplication.class);
    }

}
