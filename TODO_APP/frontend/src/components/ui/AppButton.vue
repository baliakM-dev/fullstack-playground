<script setup lang="ts">
defineProps<{
  type?: "button" | "submit";
  variant?: "primary" | "secondary" | "danger";
  disabled?: boolean;
}>();
</script>

<template>
  <button
      class="btn"
      :class="variant ?? 'primary'"
      :type="type ?? 'button'"
      :disabled="disabled"
  >
    <slot />
  </button>
</template>

<style scoped>
.btn {
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid #ddd;
  cursor: pointer;
  font-weight: 600;
}
.btn.primary {
  background: #111;
  color: #fff;
  border-color: #111;
}
.btn.secondary {
  background: #fff;
  color: #111;
}
.btn.danger {
  background: #fff;
  color: #b00020;
  border-color: #f2c0c7;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
