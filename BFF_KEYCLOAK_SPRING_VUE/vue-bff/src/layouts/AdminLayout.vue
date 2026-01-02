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
  <v-app>
    <v-app-bar color="secondary" dark flat>
      <v-app-bar-title>
        <div class="d-flex align-center ga-2">
          <v-icon>mdi-shield-check</v-icon>
          Admin dashboard
        </div>
      </v-app-bar-title>

      <v-btn to="/admin" variant="text" prepend-icon="mdi-view-dashboard">
        Prehľad
      </v-btn>
      <v-btn to="/admin/users" variant="text" prepend-icon="mdi-account-multiple">
        Používatelia
      </v-btn>

      <v-spacer />

      <v-btn
          to="/"
          variant="text"
          prepend-icon="mdi-arrow-left"
      >
        Späť do aplikácie
      </v-btn>

      <v-menu>
        <template #activator="{ props }">
          <v-btn v-bind="props" variant="text">
            {{ displayName }}
            <v-icon end>mdi-account</v-icon>
          </v-btn>
        </template>

        <v-list>
          <v-list-item>
            <v-list-item-title>{{ auth.me?.email }}</v-list-item-title>
            <v-list-item-subtitle>Admin</v-list-item-subtitle>
          </v-list-item>

          <v-divider />

          <v-list-item @click="auth.logout">
            <v-list-item-title>Odhlásiť</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main>
      <v-container class="py-6">
        <v-alert
            type="success"
            variant="tonal"
            border="start"
            class="mb-4"
            title="Admin zóna"
        >
          Rýchly prístup k dashboardu a správe používateľov.
        </v-alert>
        <RouterView />
      </v-container>
    </v-main>
  </v-app>
</template>