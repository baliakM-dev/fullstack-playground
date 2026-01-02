<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
onMounted(() => auth.loadMe());

const roles = computed(() => auth.me?.roles ?? []);
</script>

<template>
  <div>
    <!-- MAIN APP MENU -->
    <nav style="display:flex; gap:12px; align-items:center; padding:12px;">
      <RouterLink to="/">Home</RouterLink>

      <RouterLink
          v-if="roles.includes('hr') || roles.includes('admin')"
          to="/hr"
      >
        HR
      </RouterLink>

      <RouterLink
          v-if="roles.includes('admin')"
          to="/admin"
      >
        Admin
      </RouterLink>

      <span style="margin-left:auto">
        <RouterLink v-if="!auth.me" to="/login">Login</RouterLink>
        <button v-else @click="auth.logout">Logout</button>
      </span>
    </nav>

    <RouterView />
  </div>
</template>
