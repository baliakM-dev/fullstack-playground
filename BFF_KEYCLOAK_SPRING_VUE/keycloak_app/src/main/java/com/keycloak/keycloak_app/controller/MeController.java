package com.keycloak.keycloak_app.controller;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

/**
 * CURRENT USER ENDPOINT (/api/me)
 * <p>
 * EN:
 * Lightweight endpoint used by the frontend to retrieve
 * information about the currently authenticated user.
 * <p>
 * This endpoint is typically called:
 * - on application startup
 * - on page refresh
 * - by route guards (role-based navigation)
 * <p>
 * SK:
 * Jednoduchý endpoint používaný frontendom na získanie
 * informácií o aktuálne prihlásenom používateľovi.
 * <p>
 * Tento endpoint sa typicky volá:
 * - pri štarte aplikácie
 * - pri refreshi stránky
 * - v route guardoch (role-based navigácia)
 * <p>
 * -------------------------------------------------------------
 * IMPORTANT:
 * - This endpoint does NOT perform authorization logic.
 * - It only exposes identity data already verified by Spring Security.
 * -------------------------------------------------------------
 */
@RestController
@RequestMapping("/api")
public class MeController {

    /**
     * =============================================================
     * GET /api/me
     * =============================================================
     * <p>
     * EN:
     * Returns basic identity and authorization data
     * of the currently authenticated user.
     * <p>
     * Security:
     *  - Access is restricted by Spring Security configuration
     *  - Requires to be authenticated session
     * <p>
     * SK:
     * Vracia základné identifikačné a autorizačné údaje
     * o aktuálne prihlásenom používateľovi.
     * <p>
     * Bezpečnosť:
     *  - Prístup je riadený Spring Security konfiguráciou
     *  - Vyžaduje autentifikovanú session
     */
    @GetMapping("/me")
    public Map<String, Object> me(
            /**
             * EN:
             * Injects the currently authenticated OIDC user
             * from the Spring Security context.
             * <p>
             * SK:
             * Injektuje aktuálne prihláseného OIDC používateľa
             * zo Spring Security kontextu.
             */
            @AuthenticationPrincipal OidcUser user
    ) {
        /**
         * =========================================================
         * EXTRACT APPLICATION ROLES
         * =========================================================
         * <p>
         * EN:
         * Spring Security stores roles as GrantedAuthority
         * with the "ROLE_" prefix.
         * <p>
         * This logic:
         *  - filters only application roles
         *  - removes the ROLE_ prefix
         *  - sorts roles for deterministic output
         * <p>
         * SK:
         * Spring Security ukladá roly ako GrantedAuthority
         * s prefixom "ROLE_".
         * <p>
         * Táto logika:
         *  - vyfiltruje iba aplikačné roly
         *  - odstráni prefix ROLE_
         *  - zoradí roly pre deterministický výstup
         */
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .sorted()
                .toList();
        /**
         * =========================================================
         * RESPONSE PAYLOAD
         * =========================================================
         * <p>
         * EN:
         * Exposes only non-sensitive user data required by frontend:
         *  - username (technical identifier)
         *  - full name
         *  - email
         *  - application roles
         * <p>
         * SK:
         * Vystavuje iba ne-citlivé údaje potrebné pre frontend:
         *  - username (technický identifikátor)
         *  - celé meno
         *  - email
         *  - aplikačné roly
         */
        return Map.of(
                "username", user.getPreferredUsername(),
                "name", user.getFullName(),
                "email", user.getEmail(),
                "roles", roles
        );
    }
}
