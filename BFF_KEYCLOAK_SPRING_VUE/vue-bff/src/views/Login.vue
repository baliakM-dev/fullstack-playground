<script setup lang="ts">
import { onMounted } from "vue";
import { loginUrl } from "../lib/env";

onMounted(() => {
  window.location.href = loginUrl;
});
</script>

<template>
  <p>Presmerúvam na prihlásenie…</p>
</template>
