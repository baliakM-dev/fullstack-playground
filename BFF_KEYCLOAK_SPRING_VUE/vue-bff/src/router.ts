import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "./stores/auth";
import MainLayout from "./layouts/MainLayout.vue";
import AdminLayout from "./layouts/AdminLayout.vue";
import AdminHome from "./views/admin/AdminHome.vue";
import AdminUsers from "./views/admin/AdminUsers.vue";
import Hr from "./views/Hr.vue";
import Login from "./views/Login.vue";
import Home from "./views/Home.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 🟢 MAIN APP
        {
            path: "/",
            component: MainLayout,
            children: [
                { path: "", component: Home },
                { path: "login", component: Login },
                {
                    path: "hr",
                    component: Hr,
                    meta: { roles: ["hr", "admin"] },
                },
            ],
        },

        // 🔴 ADMIN
        {
            path: "/admin",
            component: AdminLayout,
            meta: { roles: ["admin"] },
            children: [
                { path: "", component: AdminHome },
                { path: "users", component: AdminUsers },
            ],
        },
    ],
});

/**
 * 🔐 GLOBAL ROUTE GUARD
 */
router.beforeEach(async (to) => {
    const auth = useAuthStore();

    // Načítaj /me len raz
    if (!auth.loaded) {
        await auth.loadMe();
    }

    const requiredRoles = (to.meta.roles as string[]) ?? [];

    // Route bez role → voľná
    if (requiredRoles.length === 0) return true;

    // Neprihlásený → login
    if (!auth.me) {
        return "/login";
    }

    // Over roly
    const allowed = requiredRoles.some(role =>
        auth.me!.roles.includes(role)
    );

    return allowed ? true : "/";
});

export default router;
