<script setup lang="ts">
import { reactive, watchEffect } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import AppField from "@/components/ui/AppField.vue";
import type { Project } from "@/api/projects";

const props = defineProps<{
  modelValue: boolean;
  project?: Project | null;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "save", payload: { name: string; description?: string | null }): void;
}>();

const form = reactive({
  name: "",
  description: "" as string,
});

const errors = reactive<{ name?: string }>({});

watchEffect(() => {
  if (props.project) {
    form.name = props.project.name;
    form.description = props.project.description ?? "";
  } else {
    form.name = "";
    form.description = "";
  }
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

  emit("save", {
    name: form.name.trim(),
    description: form.description.trim() ? form.description.trim() : null,
  });
  close();
}
</script>

<template>
  <div v-if="modelValue" class="modal">
    <div class="card">
      <h3>{{ project ? "Edit project" : "New project" }}</h3>

      <form class="grid" @submit.prevent="submit">
        <AppField label="Name" :error="errors.name">
          <input v-model="form.name" class="input" maxlength="120" />
        </AppField>

        <AppField label="Description">
          <textarea v-model="form.description" class="input" rows="3" maxlength="2000" />
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
.modal {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,.35);
  display: grid;
  place-items: center;
  padding: 16px;
}
.card {
  width: min(560px, 100%);
  background: #fff;
  border-radius: 14px;
  border: 1px solid #eee;
  padding: 16px;
}
.grid { display: grid; gap: 12px; margin-top: 12px; }
.row { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
.input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 8px 10px;
}
</style>
