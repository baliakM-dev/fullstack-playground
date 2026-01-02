package com.keycloak.keycloak_app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.*;

@Configuration
@EnableMethodSecurity
// EN: Enables method-level security (@PreAuthorize, @Secured, etc.)
// SK: Povoľuje zabezpečenie na úrovni metód (@PreAuthorize, @Secured, ...)
public class SecurityConfig {

    // EN: Public frontend URL (Vue dev server on :3000).
    //     Used for CORS, OAuth2 login redirect and logout redirect.
    // SK: Verejná URL frontend (Vue dev server na :3000).
    //     Používa sa pre CORS, redirect po login-e a logout-e.
    @Value("${app.frontend-url}")
    String frontendUrl;

    // EN: Content-Security-Policy string loaded from application.yml.
    // SK: Content-Security-Policy reťazec načítaný z application.yml.
    @Value("${app.csp.policy}")
    String cspPolicy;


    /**
     * MAIN SECURITY FILTER CHAIN
     *
     * <p>
     * EN:
     * Defines the complete Spring Security behavior:
     * - CORS
     * - CSRF
     * - Session management
     * - Security headers
     * - OAuth2 login (Keycloak)
     * - Logout
     * <p>
     * SK:
     * Definuje kompletné správanie Spring Security:
     * - CORS-CSRF-správu session-bezpečnostné hlavičky
     * - OAuth2 login (Keycloak)
     * - logout
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) {
        http
                /**
                 * =========================================================
                 * CORS
                 * =========================================================
                 * <p>
                 * EN:
                 * Allows requests from the Vue frontend.
                 * Since we use cookies (session-based auth), the following
                 * is REQUIRED:
                 *  - allowCredentials = true
                 *  - explicit allowed origin (no wildcard)
                 * <p>
                 * SK:
                 * Povoľuje volania z Vue frontendu.
                 * Keďže používame cookies (session auth), MUSÍME:
                 *  - povoliť credentials-presne definovať origin (žiadne *)
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                /**
                 * =========================================================
                 * CSRF
                 * =========================================================
                 * <p>
                 * EN:
                 * Cookie-based authentication requires CSRF protection.
                 * <p>
                 * CookieCsrfTokenRepository:
                 *  - stores CSRF token in a cookie
                 *  - frontend sends it as X-XSRF-TOKEN header
                 * <p>
                 * CSRF is disabled for:
                 *  - OAuth2 redirect endpoints
                 *  - logout (handled via a browser POST form)
                 * <p>
                 * SK:
                 * Cookie-based autentifikácia vyžaduje CSRF ochranu.
                 * <p>
                 * CookieCsrfTokenRepository:
                 *  - ukladá CSRF token do cookie
                 *  - frontend ho posiela ako X-XSRF-TOKEN header
                 * <p>
                 * CSRF je vypnuté pre:
                 *  - OAuth2 redirect endpoint
                 *  - logout (riešený browser POST formou)
                 */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        )
                        .ignoringRequestMatchers(
                                "/logout",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        )
                )

                /**
                 * =========================================================
                 * SESSION HARDENING
                 * =========================================================
                 * <p>
                 * EN:
                 * Session fixation protection:
                 *  - a NEW session is created after successful login
                 *  - the old (pre-login) session is discarded
                 * <p>
                 * SK:
                 * Ochrana proti session fixation:
                 *  - po úspešnom login-e sa vytvorí NOVÁ session
                 *  - stará session (pred login) sa zahodí
                 */
                .sessionManagement(session -> session
                        .sessionFixation(
                                SessionManagementConfigurer
                                        .SessionFixationConfigurer::migrateSession
                        )
                )

                /**
                 * =========================================================
                 * SECURITY HEADERS
                 * =========================================================
                 * <p>
                 * EN:
                 * Even though the backend acts mainly as an API,
                 * it still communicates with browsers and must be hardened
                 * against common web attacks.
                 * <p>
                 * SK:
                 * Aj keď backend slúži hlavne ako API,
                 * komunikuje s prehliadačom a musí byť zabezpečený.
                 */
                .headers(headers -> {

                    /**
                     * Content-Security-Policy (CSP)
                     * <p>
                     * EN:
                     * Development configuration:
                     *  - allows inline scripts (Vite / HMR)
                     *  - allows websocket to localhost:3000
                     * <p>
                     * SK:
                     * Vývojová konfigurácia:
                     *  - povoľuje inline skripty (Vite / HMR)
                     *  - povoľuje websocket na localhost:3000
                     */
                    headers.contentSecurityPolicy(csp ->
                            csp.policyDirectives(cspPolicy)
                    );

                    /**
                     * X-Frame-Options: DENY
                     * <p>
                     * EN: Prevents the application from being embedded in iframes
                     *     (clickjacking protection).
                     * SK: Zabráni vloženiu aplikácie do iframe
                     *     (ochrana proti carjacking).
                     */
                    headers.frameOptions(
                            HeadersConfigurer.FrameOptionsConfig::deny
                    );

                    /**
                     * Referrer-Policy
                     * <p>
                     * EN: Do not leak referrer information to other origins.
                     * SK: Neposielať referrer informácie iným doménam.
                     */
                    headers.referrerPolicy(ref ->
                            ref.policy(
                                    ReferrerPolicyHeaderWriter
                                            .ReferrerPolicy.NO_REFERRER
                            )
                    );

                    /**
                     * Permissions-Policy
                     * <p>
                     * EN: Explicitly disables access to sensitive browser features.
                     * SK: Explicitne zakazuje prístup k citlivým funkciám prehliadača.
                     */
                    headers.addHeaderWriter(
                            new StaticHeadersWriter(
                                    "Permissions-Policy",
                                    "camera=(), microphone=(), geolocation=(), payment=(), usb=()"
                            )
                    );

                    /**
                     * X-Content-Type-Options: nos niff
                     * <p>
                     * EN: Prevents MIME type sniffing.
                     * SK: Zabráni hádaniu MIME typov prehliadačom.
                     */
                    headers.contentTypeOptions(Customizer.withDefaults());
                })

                /**
                 * =========================================================
                 * AUTHORIZATION RULES
                 * =========================================================
                 * <p>
                 * EN:
                 * Backend is the SINGLE source of truth for authorization.
                 * Frontend only hides or shows UI elements.
                 * <p>
                 * SK:
                 * Backend je JEDINÁ autorita pre autorizáciu.
                 * Frontend len skrýva alebo zobrazuje UI.
                 */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/error",
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/me").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("admin")
                        .requestMatchers("/api/hr/**").hasAnyRole("hr", "admin")

                        .anyRequest().authenticated()
                )


                /**
                 * =========================================================
                 * OAUTH2 LOGIN (KEYCLOAK)
                 * =========================================================
                 * <p>
                 * EN:
                 * OAuth2 Authorization Code Flow:
                 *  - browser redirects to Keycloak
                 *  - Keycloak authenticates the user
                 *  - backend creates a session
                 * <p>
                 * SK:
                 * OAuth2 Authorization Code Flow:
                 *  - prehliadač presmeruje používateľa na Keycloak, Keycloak overí používateľa a backend vytvorí session
                 */
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect(frontendUrl)
                        )
                        // EN/SK: Custom OIDC user service for role mapping and custom claims
                        .userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(
                                        new CustomOidcUserService()
                                )
                        )
                )

                /**
                 * =========================================================
                 * LOGOUT (OIDC RP-INITIATED LOGOUT)
                 * =========================================================
                 * <p>
                 * EN:
                 *  - POST /logout
                 *  - Spring invalidates local session
                 *  - redirect to Keycloak logout
                 *  - Keycloak invalidates SSO session
                 *  - redirect back to frontend
                 * <p>
                 * SK:
                 *  - POST /logout
                 *  - Spring zruší lokálnu session
                 *  - presmerovanie na Keycloak logout
                 *  - Keycloak zruší SSO session
                 *  - presmerovanie späť na frontend
                 */
                .logout(logout -> {
                    var handler =
                            new OidcClientInitiatedLogoutSuccessHandler(
                                    clientRegistrationRepository
                            );
                    handler.setPostLogoutRedirectUri(
                            URI.create(frontendUrl).toString()
                    );
                    logout.logoutSuccessHandler(handler);
                });

        return http.build();
    }

    /**
     * =============================================================
     * CORS CONFIGURATION SOURCE
     * =============================================================
     * <p>
     * EN:
     * Dedicated CORS configuration:
     *  - explicit frontend origin
     *  - cookies enabled
     *  - allowed HTTP methods
     * <p>
     * SK:
     * Samostatná CORS konfigurácia:
     *  - presne definovaný frontend origin
     *  - povolené cookies (session)
     *  - povolené HTTP metódy
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowedOrigins(List.of(frontendUrl));
        cfg.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        cfg.setAllowedHeaders(
                List.of("Content-Type", "X-XSRF-TOKEN")
        );
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);

        return source;
    }
}
