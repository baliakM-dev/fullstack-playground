<script setup lang="ts">
import { computed } from "vue";
import { useAuthStore } from "../../stores/auth";

const auth = useAuthStore();
const roles = computed(() => auth.me?.roles ?? []);
</script>

<template>
  <v-container class="pa-0">
    <v-row>
      <v-col cols="12" md="7">
        <v-card elevation="2">
          <v-card-title class="d-flex align-center ga-2">
            <v-icon color="secondary">mdi-account-star</v-icon>
            Admin profil
          </v-card-title>
          <v-card-subtitle class="pb-0">
            Základné údaje prihláseného administrátora.
          </v-card-subtitle>
          <v-card-text>
            <v-list lines="one">
              <v-list-item
                  prepend-icon="mdi-account"
                  title="Meno"
                  :subtitle="auth.me?.name || auth.me?.username"
              />
              <v-list-item
                  prepend-icon="mdi-email"
                  title="Email"
                  :subtitle="auth.me?.email"
              />
              <v-list-item
                  prepend-icon="mdi-account-cog"
                  title="Používateľské meno"
                  :subtitle="auth.me?.username || '—'"
              />
            </v-list>
            <div class="d-flex align-center ga-2 flex-wrap mt-2">
              <v-chip
                  v-for="role in roles"
                  :key="role"
                  color="primary"
                  variant="tonal"
                  prepend-icon="mdi-shield-check"
              >
                {{ role }}
              </v-chip>
              <v-chip
                  v-if="roles.length === 0"
                  color="grey"
                  variant="tonal"
              >
                Bez rolí
              </v-chip>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      \
    </v-row>
  </v-container>
</template>