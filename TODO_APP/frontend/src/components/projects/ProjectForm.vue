<script setup lang="ts">
import { computed, reactive, watch } from "vue";
import type { Project } from "@/api/projects";

const props = defineProps<{
  modelValue: boolean;
  project: Project | null;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "save", payload: { name: string; description?: string | null }): void;
}>();

// proxy pre v-model (lebo props sú readonly)
const open = computed({
  get: () => props.modelValue,
  set: (v: boolean) => emit("update:modelValue", v),
});

const form = reactive({
  name: "",
  description: "" as string | null,
});

watch(
    () => props.project,
    (p) => {
      form.name = p?.name ?? "";
      form.description = p?.description ?? null;
    },
    { immediate: true }
);

function close() {
  open.value = false;
}

function submit() {
  emit("save", {
    name: form.name.trim(),
    description: form.description?.trim() || null,
  });
}
</script>

<template>
  <v-dialog v-model="open" max-width="640">
    <v-card rounded="xl">
      <v-card-title class="text-h6">
        {{ project ? "Edit project" : "New project" }}
      </v-card-title>

      <v-card-text class="pt-2">
        <v-text-field
            v-model="form.name"
            label="Name"
            placeholder="e.g. Work"
            required
        />

        <v-textarea
            v-model="form.description"
            label="Description"
            placeholder="Optional"
            rows="3"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="close">Cancel</v-btn>
        <v-btn color="primary" @click="submit">
          {{ project ? "Save" : "Create" }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
