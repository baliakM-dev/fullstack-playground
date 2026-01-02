import { defineStore } from "pinia";
import { api } from "../lib/api";
import { loginUrl, logoutUrl } from "../lib/env";

export type Me = {
    username: string;
    email?: string;
    name?: string;
    roles: string[];
};

export const useAuthStore = defineStore("auth", {
    state: () => ({
        me: null as Me | null,
        loaded: false,
        checked: false,
    }),

    getters: {
        isAuthed: (s) => !!s.me,
        hasRole: (s) => (role: string) => !!s.me?.roles?.includes(role),
    },

    actions: {
        async loadMe() {
            if (this.checked) return; // 👈 kľúčové

            try {
                const { data } = await api.get<Me>("/api/me");
                this.me = data;
            } catch (e: any) {
                if (e.response?.status === 401) {
                    this.me = null; // neprihlásený user = OK
                } else {
                    // console.error("❌ loadMe failed", e);
                    this.me = null;
                }
            } finally {
                this.loaded = true;
                this.checked = true; // 👈 MUSÍ BYŤ TU
            }
        },


        // 🔐 LOGIN = browser redirect
        loginRedirect() {
            window.location.href = loginUrl;
        },

        // 🔐 LOGOUT = browser redirect (❗ žiadny axios ❗)
        logout() {
            const form = document.createElement("form");
            form.method = "POST";
            form.action = logoutUrl;

            document.body.appendChild(form);
            form.submit();
        },
    },
});
