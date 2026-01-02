<script setup lang="ts">
defineProps<{
  label: string;
  error?: string;
}>();
</script>

<template>
  <label class="field">
    <span class="label">{{ label }}</span>
    <div class="control">
      <slot />
    </div>
    <span v-if="error" class="error">{{ error }}</span>
  </label>
</template>

<style scoped>
.field {
  display: grid;
  gap: 6px;
}
.label {
  font-size: 12px;
  color: #444;
}
.error {
  color: #b00020;
  font-size: 12px;
}
</style>
