const apiUrlEnv = import.meta.env.VITE_API_URL as string | undefined;
const loginUrlEnv = import.meta.env.VITE_LOGIN_URL as string | undefined;
const logoutUrlEnv = import.meta.env.VITE_LOGOUT_URL as string | undefined;

if (!apiUrlEnv) {
    console.warn("VITE_API_URL is not set. Falling back to window.location.origin.");
}

if (!loginUrlEnv) {
    console.warn("VITE_LOGIN_URL is not set. Falling back to `${VITE_API_URL || window.location.origin}/oauth2/authorization/keycloak`.");
}

if (!logoutUrlEnv) {
    console.warn("VITE_LOGOUT_URL is not set. Falling back to `${VITE_API_URL || window.location.origin}/logout`.");
}

const apiBaseUrl = apiUrlEnv || window.location.origin;
const defaultAuthBase = apiBaseUrl;

const loginUrl = loginUrlEnv || `${defaultAuthBase}/oauth2/authorization/keycloak`;
const logoutUrl = logoutUrlEnv || `${defaultAuthBase}/logout`;

export { apiBaseUrl, loginUrl, logoutUrl };