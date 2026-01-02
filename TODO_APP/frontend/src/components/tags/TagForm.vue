<script setup lang="ts">
import { reactive, watchEffect } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import AppField from "@/components/ui/AppField.vue";
import type { Tag } from "@/api/tags";

const props = defineProps<{
  modelValue: boolean;
  tag?: Tag | null;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "save", payload: { name: string }): void;
}>();

const form = reactive({ name: "" });
const errors = reactive<{ name?: string }>({});

watchEffect(() => {
  form.name = props.tag?.name ?? "";
  errors.name = undefined;
});

function close() {
  emit("update:modelValue", false);
}

function submit() {
  errors.name = undefined;

  if (!form.name.trim()) {
    errors.name = "Name is required";
    return;
  }

  emit("save", { name: form.name.trim() });
  close();
}
</script>

<template>
  <div v-if="modelValue" class="modal">
    <div class="card">
      <h3>{{ tag ? "Edit tag" : "New tag" }}</h3>

      <form class="grid" @submit.prevent="submit">
        <AppField label="Name" :error="errors.name">
          <input v-model="form.name" class="input" maxlength="60" />
        </AppField>

        <div class="row">
          <AppButton variant="secondary" @click="close">Cancel</AppButton>
          <AppButton type="submit">Save</AppButton>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal { position: fixed; inset: 0; background: rgba(0,0,0,.35); display: grid; place-items: center; padding: 16px; }
.card { width: min(520px, 100%); background: #fff; border-radius: 14px; border: 1px solid #eee; padding: 16px; }
.grid { display: grid; gap: 12px; margin-top: 12px; }
.row { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
.input { width: 100%; border: 1px solid #ddd; border-radius: 10px; padding: 8px 10px; }
</style>
