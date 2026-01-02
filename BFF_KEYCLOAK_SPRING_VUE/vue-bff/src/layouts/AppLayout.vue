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
    <v-app-bar color="primary" dark>
      <!-- App title -->
      <v-app-bar-title>
        BFF App
      </v-app-bar-title>

      <!-- Main navigation -->
      <v-btn to="/" variant="text">Home</v-btn>
      <v-btn v-if="isHr" to="/hr" variant="text">HR</v-btn>
      <v-btn v-if="isAdmin" to="/admin" variant="text">Admin</v-btn>

      <v-spacer />

      <!-- Auth section -->
      <template v-if="auth.me">
        <v-menu>
          <template #activator="{ props }">
            <v-btn v-bind="props" variant="text">
              {{ auth.me.name || auth.me.username }}
              <v-icon end>mdi-account</v-icon>
            </v-btn>
          </template>

          <v-list>
            <v-list-item>
              <v-list-item-title>{{ auth.me.email }}</v-list-item-title>
            </v-list-item>

            <v-divider />

            <v-list-item @click="auth.logout">
              <v-list-item-title>Logout</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>

      <v-btn v-else to="/login" variant="outlined">
        Login
      </v-btn>
    </v-app-bar>

    <!-- Page content -->
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>
