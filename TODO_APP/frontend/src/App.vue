<script setup lang="ts">
import {ref} from "vue";
import {useRoute} from "vue-router";

const drawer = ref(true);
const route = useRoute();

const links = [
  {title: "Todos", to: "/todos", icon: "mdi-check-circle-outline"},
  {title: "Projects", to: "/projects", icon: "mdi-folder-outline"},
  {title: "Tags", to: "/tags", icon: "mdi-tag-outline"},
];

function isActive(path: string) {
  return route.path.startsWith(path);
}
</script>

<template>
  <v-app>
    <v-navigation-drawer
        v-model="drawer"
        :temporary="$vuetify.display.smAndDown"
        :permanent="!$vuetify.display.smAndDown"
        width="280"
    >
      <v-list>
        <v-list-item
            title="TODO App"
            subtitle="Spring Boot + Vue + Vuetify"
            prepend-icon="mdi-view-dashboard-outline"
        />
      </v-list>

      <v-divider/>

      <v-list nav density="comfortable">
        <v-list-item
            v-for="l in links"
            :key="l.to"
            :to="l.to"
            :prepend-icon="l.icon"
            :title="l.title"
            :active="isActive(l.to)"
        />
      </v-list>

      <template #append>
        <v-divider/>
        <v-list density="compact">
          <v-list-item
              title="Swagger UI"
              subtitle="Backend docs"
              prepend-icon="mdi-book-open-variant"
              href="http://localhost:8080/swagger-ui"
              target="_blank"
          />
        </v-list>
      </template>
    </v-navigation-drawer>

    <v-app-bar elevation="1">
      <v-app-bar-nav-icon @click="drawer = !drawer"/>
      <v-app-bar-title>{{ route.meta?.title ?? "TODO" }}</v-app-bar-title>
      <v-spacer/>
      <v-btn
          icon="mdi-github"
          variant="text"
          href="#"
          disabled
          title="(optional) repo link"
      />
    </v-app-bar>

    <v-main>
      <v-container class="py-6" fluid>
        <RouterView/>
      </v-container>
    </v-main>
  </v-app>
</template>
