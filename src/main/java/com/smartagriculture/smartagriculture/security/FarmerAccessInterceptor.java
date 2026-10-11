package com.smartagriculture.smartagriculture.security;

import com.smartagriculture.smartagriculture.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * FarmerAccessInterceptor runs before every controller method.
 * It looks for a "farmerId" in the URL path (/farmer/5/...) and in the query (?farmerId=5).
 * If it is NOT the logged-in farmer's id, the request is refused with 403.
 *
 * Because it is automatic, any future endpoint that uses a "farmerId" in the URL
 * is protected without extra code.
 */
public class FarmerAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long loggedInFarmerId = CurrentFarmer.idOrNull();
        if (loggedInFarmerId == null) {
            // Nobody logged in: the security filters already decide (401), nothing to compare.
            return true;
        }

        // 1. The path, for example /api/crops/farmer/{farmerId}
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            check(loggedInFarmerId, pathVariables.get("farmerId"));
        }

        // 2. The query, for example ?farmerId=5
        // getParameterValues returns EVERY value, so "?farmerId=5&farmerId=6" cannot sneak through.
        String[] queryValues = request.getParameterValues("farmerId");
        if (queryValues != null) {
            for (String value : queryValues) {
                check(loggedInFarmerId, value);
            }
        }

        return true; // all checks passed, go on to the controller
    }

    // Throws 403 if the supplied id is a number different from the logged-in farmer's id.
    private void check(Long loggedInFarmerId, String supplied) {
        if (supplied == null) {
            return;
        }
        Long suppliedId;
        try {
            suppliedId = Long.valueOf(supplied);
        } catch (NumberFormatException e) {
            return; // not a number: the normal request handling will answer 400
        }
        if (!loggedInFarmerId.equals(suppliedId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "error.ownDataOnly");
        }
    }
}