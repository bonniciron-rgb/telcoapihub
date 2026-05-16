package com.telcoapihub.security;

import com.telcoapihub.domain.Enterprise;
import com.telcoapihub.repository.EnterpriseRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Resolves the {@code X-API-Key} header into an authenticated principal.
 *
 * <p>Scaffold stand-in for CAMARA ICM: northbound auth is OAuth2/OIDC with
 * client-credentials (2-legged) and CIBA/auth-code (3-legged) flows. The
 * key model keeps the rest of the hub testable until ICM is wired in.
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-Key";

    private final EnterpriseRepository enterpriseRepository;
    private final String adminApiKey;

    public ApiKeyAuthFilter(EnterpriseRepository enterpriseRepository, String adminApiKey) {
        this.enterpriseRepository = enterpriseRepository;
        this.adminApiKey = adminApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = request.getHeader(HEADER);
        if (StringUtils.hasText(key) && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (key.equals(adminApiKey)) {
                authenticate("admin", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            } else {
                enterpriseRepository.findByApiKey(key)
                        .filter(Enterprise::isActive)
                        .ifPresent(e -> authenticate(e, List.of(new SimpleGrantedAuthority("ROLE_ENTERPRISE"))));
            }
        }
        chain.doFilter(request, response);
    }

    private void authenticate(Object principal, List<SimpleGrantedAuthority> authorities) {
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
