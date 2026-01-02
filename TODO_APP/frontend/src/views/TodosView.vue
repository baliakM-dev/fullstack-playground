<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import TodoForm from "../components/todo/TodoForm.vue";
import { useProjectsStore } from "@/stores/projects";
import { useTagsStore } from "@/stores/tags";
import { useTodosStore } from "@/stores/todos";
import type { Todo } from "@/api/todos";

const projects = useProjectsStore();
const tags = useTagsStore();
const todos = useTodosStore();

const open = ref(false);
const editing = ref<Todo | null>(null);

const filterProjectId = ref<string>("");
const filterStatus = ref<string>("");

onMounted(async () => {
  if (projects.items.length === 0) await projects.load();
  if (tags.items.length === 0) await tags.load();
  await loadTodos();
});

async function loadTodos() {
  const params: any = {};
  if (filterProjectId.value) params.projectId = filterProjectId.value;
  if (filterStatus.value) params.status = filterStatus.value;
  await todos.load(params);
}

watch([filterProjectId, filterStatus], loadTodos);

function newTodo() {
  editing.value = null;
  open.value = true;
}

function edit(todo: Todo) {
  editing.value = todo;
  open.value = true;
}

async function save(payload: any) {
  if (editing.value) await todos.update(editing.value.id, payload);
  else await todos.create(payload);
  await loadTodos();
}

async function remove(todo: Todo) {
  if (!confirm(`Delete todo "${todo.title}"?`)) return;
  await todos.remove(todo.id);
  await loadTodos();
}

const rows = computed(() => todos.items);
</script>

<template>
  <div class="page">
    <div class="header">
      <h2>Todos</h2>
      <AppButton @click="newTodo">New</AppButton>
    </div>

    <div class="filters">
      <label class="filter">
        <span>Project</span>
        <select v-model="filterProjectId" class="input">
          <option value="">All</option>
          <option v-for="p in projects.items" :key="p.id" :value="p.id">
            {{ p.name }}
          </option>
        </select>
      </label>

      <label class="filter">
        <span>Status</span>
        <select v-model="filterStatus" class="input">
          <option value="">All</option>
          <option value="OPEN">OPEN</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>
      </label>
    </div>

    <div v-if="todos.loading" class="muted">Loading...</div>

    <div class="list">
      <div v-for="t in rows" :key="t.id" class="card">
        <div class="top">
          <div class="title">{{ t.title }}</div>
          <div class="meta">
            <span class="pill">{{ t.status }}</span>
            <span class="pill">{{ t.priority }}</span>
            <span class="pill">Project: {{ t.projectName }}</span>
            <span v-if="t.dueDate" class="pill">Due: {{ t.dueDate }}</span>
          </div>
        </div>

        <div v-if="t.description" class="desc">{{ t.description }}</div>

        <div class="tagrow" v-if="t.tags.length">
          <span v-for="tag in t.tags" :key="tag.id" class="tag">#{{ tag.name }}</span>
        </div>

        <div class="actions">
          <AppButton variant="secondary" @click="edit(t)">Edit</AppButton>
          <AppButton variant="danger" @click="remove(t)">Delete</AppButton>
        </div>
      </div>

      <div v-if="!todos.loading && rows.length === 0" class="muted">
        No todos yet.
      </div>
    </div>

    <TodoForm v-model="open" :todo="editing" @save="save" />
  </div>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.filters { display: flex; gap: 10px; flex-wrap: wrap; }
.filter { display: grid; gap: 6px; font-size: 12px; color: #444; }
.input { border: 1px solid #ddd; border-radius: 10px; padding: 8px 10px; min-width: 220px; }
.list { display: grid; gap: 10px; }
.card { border: 1px solid #eee; border-radius: 14px; background: #fff; padding: 12px; display: grid; gap: 10px; }
.top { display: grid; gap: 6px; }
.title { font-weight: 900; font-size: 16px; }
.meta { display: flex; gap: 8px; flex-wrap: wrap; }
.pill { font-size: 12px; border: 1px solid #eee; padding: 4px 8px; border-radius: 999px; color: #333; }
.desc { color: #555; }
.tagrow { display: flex; gap: 8px; flex-wrap: wrap; }
.tag { font-size: 12px; border: 1px solid #eee; padding: 4px 8px; border-radius: 999px; color: #333; }
.actions { display: flex; gap: 8px; justify-content: flex-end; }
.muted { color: #777; }
</style>
