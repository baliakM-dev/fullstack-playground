package com.keycloak.keycloak_app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.*;

/**
 * CUSTOM OIDC USER SERVICE
 * <p>
 * EN:
 * Custom implementation of OidcUserService responsible for:
 * - loading the OIDC user from Keycloak (delegating to super.loadUser)
 * - extracting application roles from token claims
 * - mapping roles to Spring Security authorities (ROLE_*)
 * - preserving default OIDC and scope authorities
 * <p>
 * SK:
 * Vlastná implementácia OidcUserService zodpovedná za:
 * - načítanie OIDC používateľa z Keycloaku (delegáciou na super.loadUser)
 * - extrakciu aplikačných rolí z token claims
 * - mapovanie rolí na Spring Security autority (ROLE_*)
 * - zachovanie pôvodných OIDC a scope autorít
 * <p>
 * This class is CRITICAL for:
 * - hasRole / hasAnyRole checks
 * - correct backend authorization behavior
 */
public class CustomOidcUserService extends OidcUserService {

    /**
     * LOAD & ENRICH OIDC USER
     * <p>
     * EN:
     * Called automatically during OAuth2 / OIDC login flow.
     * This method transforms the raw OIDC user into a form usable
     * by Spring Security authorization mechanisms.
     * <p>
     * SK:
     * Volá sa automaticky počas OAuth2 / OIDC login flow.
     * Táto metóda transformuje OIDC používateľa do formy,
     * ktorú vie Spring Security použiť na autorizáciu.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        /**
         * 1️⃣ Delegate base user loading to the default implementation.
         * <p>
         * EN:
         * Loads:
         *  - ID token
         *  - UserInfo
         *  - standard OIDC claims
         *  - default authorities (OIDC_USER, SCOPE_*)
         *
         * SK:
         * Načíta:
         *  - ID token
         *  - UserInfo
         *  - štandardné OIDC claims
         *  - defaultné autority (OIDC_USER, SCOPE_*)
         */
        OidcUser oidcUser = super.loadUser(userRequest);

        /**
         * 2️⃣ Preserve existing authorities.
         * <p>
         * EN:
         * NEVER remove these authorities:
         *  - OIDC_USER
         *  - SCOPE_openid / profile / email
         * <p>
         * Spring Security relies on them internally.
         * <p>
         * SK:
         * NIKDY neodstraňuj tieto autority:
         *  - OIDC_USER
         *  - SCOPE_openid / profile / email
         * <p>
         * Spring Security ich používa interne.
         */
        Set<GrantedAuthority> mappedAuthorities =
                new HashSet<>(oidcUser.getAuthorities());

        /**
         * 3️⃣ Extract application roles from Keycloak token claims.
         * <p>
         * EN:
         * Realm roles are exposed via the "roles" claim
         * (configured via Keycloak mapper).
         * <p>
         * SK:
         * Realm roly sú dostupné v claime "roles“
         * (nastavené cez Keycloak mapper).
         */
        Object rolesClaim = oidcUser.getClaims().get("roles");

        if (rolesClaim instanceof Collection<?> roles) {

            roles.stream()
                    .map(Object::toString)

                    /**
                     * 4️⃣ Filter out Keycloak system roles.
                     * <p>
                     * EN:
                     * These roles are infrastructure-related and should
                     * NOT be used for application authorization.
                     * <p>
                     * SK:
                     * Ide o systémové roly Keycloaku, ktoré
                     * NEPATRIA do aplikačnej autorizácie.
                     */
                    .filter(role -> !role.startsWith("offline_"))
                    .filter(role -> !role.startsWith("uma_"))
                    .filter(role -> !role.startsWith("default-"))

                    /**
                     * 5️⃣ Apply Spring Security role convention.
                     * <p>
                     * EN:
                     * hasRole("admin") → checks for authority "ROLE_admin"
                     * <p>
                     * SK:
                     * hasRole("admin") → očakáva autoritu "ROLE_admin"
                     */
                    .map(role -> "ROLE_" + role)

                    /**
                     * 6️⃣ Convert role string into GrantedAuthority.
                     */
                    .map(SimpleGrantedAuthority::new)

                    /**
                     * 7️⃣ Add mapped role authorities to the final set.
                     */
                    .forEach(mappedAuthorities::add);
        }

        /**
         * 8️⃣ Return enriched OIDC user instance.
         * <p>
         * EN:
         *  - includes mapped authorities
         *  - preserves ID token and UserInfo
         *  - uses "preferred_username" as principal name
         * <p>
         * SK:
         *  - obsahuje namapované autority
         *  - zachováva ID token a UserInfo
         *  - používa "preferred_username“ ako principal name
         */
        return new DefaultOidcUser(
                mappedAuthorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "preferred_username"
        );
    }
}
