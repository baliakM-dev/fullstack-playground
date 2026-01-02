<script setup lang="ts">
import { computed, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";

const auth = useAuthStore();
onMounted(() => auth.loadMe());

const roles = computed(() => auth.me?.roles ?? []);
const isAdmin = computed(() => roles.value.includes("admin"));
const isHr = computed(() => roles.value.includes("hr") || isAdmin.value);
</script>

<template>
  <v-app>
    <v-app-bar color="primary" dark flat>
      <v-app-bar-title>BFF Demo</v-app-bar-title>
      <v-btn
          to="/"
          variant="text"
          prepend-icon="mdi-home"
      >
        Domov
      </v-btn>

      <v-btn
          v-if="isHr"
          to="/hr"
          variant="text"
          prepend-icon="mdi-account-group"
      >
        HR sekcia
      </v-btn>
      <v-btn
          v-if="isAdmin"
          to="/admin"
          variant="text"
          prepend-icon="mdi-shield-account"
      >
        Admin dashboard
      </v-btn>

      <v-spacer />

      <template v-if="auth.me">
        <v-chip
            class="mr-2"
            color="secondary"
            prepend-icon="mdi-shield-check"
            v-if="isAdmin"
        >
          Admin
        </v-chip>
        <v-chip
            class="mr-2"
            color="info"
            prepend-icon="mdi-account-group"
            v-else-if="isHr"
        >
          HR
        </v-chip>

        <v-menu>
          <template #activator="{ props }">
            <v-btn v-bind="props" variant="text">
              {{ auth.me?.name || auth.me?.username }}
              <v-icon end>mdi-account</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item>
              <v-list-item-title>{{ auth.me?.email }}</v-list-item-title>
              <v-list-item-subtitle>
                {{ roles.join(", ") || "bez rolí" }}
              </v-list-item-subtitle>
            </v-list-item>

            <v-divider />

            <v-list-item @click="auth.logout">
              <v-list-item-title>Odhlásiť</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>

      <v-btn
          v-else
          to="/login"
          variant="outlined"
          color="white"
          prepend-icon="mdi-login"
      >
        Prihlásiť
      </v-btn>
    </v-app-bar>
    <v-main>
      <v-container class="py-6">
        <v-alert
            type="info"
            variant="tonal"
            border="start"
            class="mb-4"
        >
          Toto menu je zdieľané pre celú aplikáciu a rýchlo presmeruje na Home,
          HR aj Admin dashboard.
        </v-alert>
        <RouterView />
      </v-container>
    </v-main>
  </v-app>
</template>
