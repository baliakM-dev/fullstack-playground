<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
onMounted(() => {
  if (!auth.loaded) auth.loadMe();
});

const displayName = computed(() => auth.me?.name ?? auth.me?.username ?? "");
</script>

<template>
  <div>
    <header style="display:flex; align-items:center; gap:12px; padding:12px; border-bottom:1px solid #ddd;">
      <strong>Admin</strong>

      <nav style="display:flex; gap:12px;">
        <RouterLink to="/admin">Dashboard</RouterLink>
        <RouterLink to="/admin/users">Users</RouterLink>
        <RouterLink to="/">Back to App</RouterLink>
      </nav>

      <div style="margin-left:auto; display:flex; align-items:center; gap:12px;">
        <div style="text-align:right;">
          <div>{{ displayName }}</div>
          <div style="font-size:12px; color:#777;">{{ auth.me?.email }}</div>
        </div>
<!--        <button @click="auth.logout">Logout</button>-->
        <v-btn variant="tonal" size="large" @click="auth.logout">Large Button</v-btn>
      </div>
    </header>

    <main style="padding:12px;">
      <RouterView />
    </main>
  </div>
</template>