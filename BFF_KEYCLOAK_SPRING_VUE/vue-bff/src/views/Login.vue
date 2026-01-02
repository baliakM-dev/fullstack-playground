<script setup lang="ts">
import { onMounted } from "vue";

onMounted(() => {
  window.location.href =
      "http://localhost:8080/oauth2/authorization/keycloak";
});
</script>

<template>
  <p>Presmerúvam na prihlásenie…</p>
</template>
