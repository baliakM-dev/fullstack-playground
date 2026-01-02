<script setup lang="ts">
import { computed, reactive, watchEffect } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import AppField from "@/components/ui/AppField.vue";
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
    tagIds?: string[] | null;
  }): void;
}>();

const form = reactive({
  title: "",
  description: "",
  status: "OPEN" as TodoStatus,
  priority: "MEDIUM" as TodoPriority,
  dueDate: "" as string, // yyyy-mm-dd
  projectId: "" as string,
  tagIds: [] as string[],
});

const errors = reactive<{ title?: string; projectId?: string }>({});

watchEffect(() => {
  if (props.todo) {
    form.title = props.todo.title;
    form.description = props.todo.description ?? "";
    form.status = props.todo.status;
    form.priority = props.todo.priority;
    form.dueDate = props.todo.dueDate ?? "";
    form.projectId = props.todo.projectId;
    form.tagIds = props.todo.tags.map((t) => t.id);
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
});

const tagOptions = computed(() => tagsStore.items);

function toggleTag(tagId: string) {
  const idx = form.tagIds.indexOf(tagId);
  if (idx >= 0) form.tagIds.splice(idx, 1);
  else form.tagIds.push(tagId);
}

function close() {
  emit("update:modelValue", false);
}

function submit() {
  errors.title = undefined;
  errors.projectId = undefined;

  if (!form.title.trim()) {
    errors.title = "Title is required";
    return;
  }
  if (!form.projectId) {
    errors.projectId = "Project is required";
    return;
  }

  emit("save", {
    title: form.title.trim(),
    description: form.description.trim() ? form.description.trim() : null,
    status: form.status,
    priority: form.priority,
    dueDate: form.dueDate ? form.dueDate : null,
    projectId: form.projectId,
    tagIds: form.tagIds.length ? form.tagIds : [],
  });
  close();
}
</script>

<template>
  <div v-if="modelValue" class="modal">
    <div class="card">
      <h3>{{ todo ? "Edit todo" : "New todo" }}</h3>

      <form class="grid" @submit.prevent="submit">
        <AppField label="Title" :error="errors.title">
          <input v-model="form.title" class="input" maxlength="120" />
        </AppField>

        <AppField label="Description">
          <textarea v-model="form.description" class="input" rows="3" maxlength="2000" />
        </AppField>

        <div class="cols">
          <AppField label="Status">
            <select v-model="form.status" class="input">
              <option value="OPEN">OPEN</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="DONE">DONE</option>
            </select>
          </AppField>

          <AppField label="Priority">
            <select v-model="form.priority" class="input">
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
            </select>
          </AppField>
        </div>

        <div class="cols">
          <AppField label="Due date">
            <input v-model="form.dueDate" type="date" class="input" />
          </AppField>

          <AppField label="Project" :error="errors.projectId">
            <select v-model="form.projectId" class="input">
              <option value="" disabled>Select project...</option>
              <option v-for="p in projects.items" :key="p.id" :value="p.id">
                {{ p.name }}
              </option>
            </select>
          </AppField>
        </div>

        <div class="tagbox">
          <div class="tagbox-title">Tags</div>
          <div class="taggrid">
            <label v-for="t in tagOptions" :key="t.id" class="tagitem">
              <input type="checkbox" :checked="form.tagIds.includes(t.id)" @change="toggleTag(t.id)" />
              <span>{{ t.name }}</span>
            </label>
            <div v-if="tagOptions.length === 0" class="muted">No tags yet</div>
          </div>
        </div>

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
.card { width: min(720px, 100%); background: #fff; border-radius: 14px; border: 1px solid #eee; padding: 16px; }
.grid { display: grid; gap: 12px; margin-top: 12px; }
.cols { display: grid; gap: 12px; grid-template-columns: 1fr 1fr; }
.row { display: flex; justify-content: flex-end; gap: 8px; margin-top: 6px; }
.input { width: 100%; border: 1px solid #ddd; border-radius: 10px; padding: 8px 10px; }
.tagbox { border: 1px solid #eee; border-radius: 12px; padding: 12px; }
.tagbox-title { font-size: 12px; color: #444; margin-bottom: 8px; font-weight: 600; }
.taggrid { display: flex; flex-wrap: wrap; gap: 10px 14px; }
.tagitem { display: inline-flex; gap: 8px; align-items: center; border: 1px solid #eee; padding: 6px 10px; border-radius: 999px; }
.muted { color: #777; font-size: 12px; }
@media (max-width: 640px) { .cols { grid-template-columns: 1fr; } }
</style>
