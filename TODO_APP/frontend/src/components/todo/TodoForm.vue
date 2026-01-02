<script setup lang="ts">
import { computed, reactive, watch } from "vue";
import type { Todo, TodoPriority, TodoStatus } from "@/api/todos";
import { useProjectsStore } from "@/stores/projects";
import { useTagsStore } from "@/stores/tags";

const projects = useProjectsStore();
const tagsStore = useTagsStore();

const props = defineProps<{
  modelValue: boolean;
  todo?: Todo | null;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "save", payload: {
    title: string;
    description?: string | null;
    status: TodoStatus;
    priority: TodoPriority;
    dueDate?: string | null;
    projectId: string;
    tagIds?: string[];
  }): void;
}>();

const open = computed({
  get: () => props.modelValue,
  set: (v: boolean) => emit("update:modelValue", v),
});

const form = reactive({
  title: "",
  description: "" as string,
  status: "OPEN" as TodoStatus,
  priority: "MEDIUM" as TodoPriority,
  dueDate: "" as string, // yyyy-mm-dd
  projectId: "" as string,
  tagIds: [] as string[],
});

const errors = reactive<{ title?: string; projectId?: string }>({});

watch(
    () => props.todo,
    (t) => {
      if (t) {
        form.title = t.title;
        form.description = t.description ?? "";
        form.status = t.status;
        form.priority = t.priority;
        form.dueDate = t.dueDate ?? "";
        form.projectId = t.projectId;
        form.tagIds = (t.tags ?? []).map((x) => x.id);
      } else {
        form.title = "";
        form.description = "";
        form.status = "OPEN";
        form.priority = "MEDIUM";
        form.dueDate = "";
        form.projectId = projects.items[0]?.id ?? "";
        form.tagIds = [];
      }
      errors.title = undefined;
      errors.projectId = undefined;
    },
    { immediate: true }
);

const projectItems = computed(() =>
    projects.items.map((p) => ({ title: p.name, value: p.id }))
);

const tagItems = computed(() =>
    tagsStore.items.map((t) => ({ title: t.name, value: t.id }))
);

const statusItems = ["OPEN", "IN_PROGRESS", "DONE"];
const priorityItems = ["LOW", "MEDIUM", "HIGH"];

function close() {
  open.value = false;
}

function submit() {
  errors.title = undefined;
  errors.projectId = undefined;

  const title = form.title.trim();
  if (!title) {
    errors.title = "Title is required";
    return;
  }
  if (!form.projectId) {
    errors.projectId = "Project is required";
    return;
  }

  emit("save", {
    title,
    description: form.description.trim() ? form.description.trim() : null,
    status: form.status,
    priority: form.priority,
    dueDate: form.dueDate ? form.dueDate : null,
    projectId: form.projectId,
    tagIds: form.tagIds,
  });
}
</script>

<template>
  <v-dialog v-model="open" max-width="760">
    <v-card rounded="xl">
      <v-card-title class="text-h6">
        {{ todo ? "Edit todo" : "New todo" }}
      </v-card-title>

      <v-card-text class="pt-2">
        <v-text-field
            v-model="form.title"
            label="Title"
            maxlength="120"
            :error="!!errors.title"
            :error-messages="errors.title ? [errors.title] : []"
        />

        <v-textarea
            v-model="form.description"
            label="Description"
            maxlength="2000"
            rows="3"
        />

        <v-row dense>
          <v-col cols="12" sm="6">
            <v-select
                v-model="form.status"
                :items="statusItems"
                label="Status"
                variant="outlined"
            />
          </v-col>

          <v-col cols="12" sm="6">
            <v-select
                v-model="form.priority"
                :items="priorityItems"
                label="Priority"
                variant="outlined"
            />
          </v-col>
        </v-row>

        <v-row dense>
          <v-col cols="12" sm="6">
            <v-text-field
                v-model="form.dueDate"
                type="date"
                label="Due date"
                variant="outlined"
            />
          </v-col>

          <v-col cols="12" sm="6">
            <v-select
                v-model="form.projectId"
                :items="projectItems"
                item-title="title"
                item-value="value"
                label="Project"
                variant="outlined"
                :error="!!errors.projectId"
                :error-messages="errors.projectId ? [errors.projectId] : []"
            />
          </v-col>
        </v-row>

        <v-select
            v-model="form.tagIds"
            :items="tagItems"
            item-title="title"
            item-value="value"
            label="Tags"
            variant="outlined"
            multiple
            chips
            closable-chips
        />

        <v-alert
            v-if="tagItems.length === 0"
            type="info"
            variant="tonal"
            class="mt-3"
            text="No tags yet — create tags first if you want to label todos."
        />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="close">Cancel</v-btn>
        <v-btn color="primary" @click="submit">
          {{ todo ? "Save" : "Create" }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
