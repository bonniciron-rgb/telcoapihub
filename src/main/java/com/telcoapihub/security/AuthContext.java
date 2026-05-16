package com.telcoapihub.security;

import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.Enterprise;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience access to the authenticated principal for the current request.
 */
public final class AuthContext {

    private AuthContext() {
    }

    public static Enterprise currentEnterprise() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Enterprise enterprise) {
            return enterprise;
        }
        throw ApiException.unauthenticated("A valid enterprise API key is required");
    }
}
