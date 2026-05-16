package com.telcoapihub.config;

import com.telcoapihub.repository.EnterpriseRepository;
import com.telcoapihub.security.ApiKeyAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
            "/actuator/health", "/actuator/info", "/h2-console/**"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            EnterpriseRepository enterpriseRepository,
                                            @Value("${hub.admin-api-key}") String adminApiKey) throws Exception {
        ApiKeyAuthFilter apiKeyFilter = new ApiKeyAuthFilter(enterpriseRepository, adminApiKey);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> writeError(res, 401, "UNAUTHENTICATED",
                                "A valid API key is required"))
                        .accessDeniedHandler((req, res, e) -> writeError(res, 403, "PERMISSION_DENIED",
                                "The API key is not permitted to access this resource")));

        return http.build();
    }

    private static void writeError(jakarta.servlet.http.HttpServletResponse res,
                                   int status, String code, String message) throws java.io.IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write(
                "{\"status\":" + status + ",\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }
}
