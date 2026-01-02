import axios from "axios";
import { apiBaseUrl } from "./env";

export const api = axios.create({
    baseURL: apiBaseUrl,
    withCredentials: true,
});

// 🔐 CSRF interceptor (FIX)
api.interceptors.request.use((config) => {
    const match = document.cookie
        .split("; ")
        .find(c => c.startsWith("XSRF-TOKEN="));

    if (match) {
        const token = match.split("=")[1];
        config.headers["X-XSRF-TOKEN"] = token;
    }

    return config;
});
