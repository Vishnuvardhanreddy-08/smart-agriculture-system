package com.smartagriculture.smartagriculture.security;

import com.smartagriculture.smartagriculture.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * CurrentFarmer answers one question: "Which farmer is logged in right now?"
 * The answer comes from the TOKEN (set by JwtAuthenticationFilter), never from the URL.
 */
public final class CurrentFarmer {

    private CurrentFarmer() {
        // No objects needed: we only use the static methods below.
    }

    // The logged-in farmer's id, or null if nobody is logged in.
    public static Long idOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long farmerId) {
            return farmerId;
        }
        return null;
    }

    // The logged-in farmer's id. Throws 401 if nobody is logged in.
    public static Long id() {
        Long farmerId = idOrNull();
        if (farmerId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "error.unauthorized");
        }
        return farmerId;
    }

    // Makes sure the farmerId sent by the client is the logged-in farmer's own id.
    // If not, the answer is 403 Forbidden.
    public static void requireSame(Long suppliedFarmerId) {
        if (suppliedFarmerId == null || !suppliedFarmerId.equals(id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "error.ownDataOnly");
        }
    }
}