package software.baig.helios.security;

import java.io.IOException;
import java.util.regex.Pattern;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;


@Configuration
public class SPARedirectFilterConfiguration {

    private static final Pattern STATIC_RESOURCE = Pattern.compile(".*\\.[a-zA-Z0-9]+$");


    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> redirectFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(createRedirectFilter());
        registration.addUrlPatterns("/ui/*");
        registration.setName("frontendRedirectFilter");
        registration.setOrder(1);

        return registration;
    }

    private OncePerRequestFilter createRedirectFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String path = request.getRequestURI();

                if (isFrontendRoute(path)) {
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/ui/index.html");
                    dispatcher.forward(request, response);

                    return;
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    private boolean isFrontendRoute(String path) {
        if (path.equals("/ui") || path.equals("/ui/")) {
            return true;
        }

        if (!path.startsWith("/ui/")) {
            return false;
        }

        if (path.equals("/ui/index.html")) {
            return false;
        }

        return !STATIC_RESOURCE.matcher(path).matches();
    }

}
