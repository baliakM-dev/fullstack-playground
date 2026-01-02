<script setup lang="ts">
import { computed, reactive, watch } from "vue";
import type { Tag } from "@/api/tags";

const props = defineProps<{
  modelValue: boolean;
  tag?: Tag | null;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "save", payload: { name: string }): void;
}>();

// proxy pre v-model (props sú readonly)
const open = computed({
  get: () => props.modelValue,
  set: (v: boolean) => emit("update:modelValue", v),
});

const form = reactive({ name: "" });
const errors = reactive<{ name?: string }>({});

watch(
    () => props.tag,
    (t) => {
      form.name = t?.name ?? "";
      errors.name = undefined;
    },
    { immediate: true }
);

function close() {
  open.value = false;
}

function submit() {
  errors.name = undefined;

  const name = form.name.trim();
  if (!name) {
    errors.name = "Name is required";
    return;
  }

  emit("save", { name });
}
</script>

<template>
  <v-dialog v-model="open" max-width="560">
    <v-card rounded="xl">
      <v-card-title class="text-h6">
        {{ tag ? "Edit tag" : "New tag" }}
      </v-card-title>

      <v-card-text class="pt-2">
        <v-text-field
            v-model="form.name"
            label="Name"
            placeholder="e.g. urgent"
            maxlength="60"
            :error="!!errors.name"
            :error-messages="errors.name ? [errors.name] : []"
            @keyup.enter="submit"
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="close">Cancel</v-btn>
        <v-btn color="primary" @click="submit">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
