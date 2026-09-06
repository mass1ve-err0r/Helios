package software.baig.helios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class SecurityConfiguration {


    @Bean
    UserDetailsService userDetailsService() {
        var passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        var user = User
                .withUsername("admin")
                .password(passwordEncoder.encode("helios-admin"))
                .roles("ADMIN")
                .build();

        var registrationUser = User
                .withUsername("device-registration-ios")
                .password(passwordEncoder.encode("some-secret"))
                .roles("REGISTRATION")
                .build();

        return new InMemoryUserDetailsManager(user, registrationUser);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/favicon.ico",
                                "/error"
                        )
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/devices/register")
                        .hasRole("REGISTRATION")

                        .requestMatchers(
                                "/ui",
                                "/ui/",
                                "/ui/**",
                                "/api/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest().denyAll()
                )
                .httpBasic(basic -> basic
                        .realmName("BS-HELIOS")
                )
                .build();
    }

}