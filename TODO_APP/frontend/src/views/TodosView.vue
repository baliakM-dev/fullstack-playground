<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import TodoForm from "@/components/todo/TodoForm.vue";
import { useProjectsStore } from "@/stores/projects";
import { useTagsStore } from "@/stores/tags";
import { useTodosStore } from "@/stores/todos";
import type { Todo, TodoListParams, TodoStatus } from "@/api/todos";

const projects = useProjectsStore();
const tags = useTagsStore();
const todos = useTodosStore();

const open = ref(false);
const editing = ref<Todo | null>(null);

const filterProjectId = ref<string>("");
const filterStatus = ref<"" | TodoStatus>("");

const confirmOpen = ref(false);
const toDelete = ref<Todo | null>(null);

onMounted(async () => {
  if (projects.items.length === 0) await projects.load();
  if (tags.items.length === 0) await tags.load();
  await loadTodos();
});

async function loadTodos() {
  const params: TodoListParams = {};
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
  open.value = false;
  await loadTodos();
}

function askDelete(todo: Todo) {
  toDelete.value = todo;
  confirmOpen.value = true;
}

async function confirmDelete() {
  if (!toDelete.value) return;
  await todos.remove(toDelete.value.id);
  confirmOpen.value = false;
  toDelete.value = null;
  await loadTodos();
}

const rows = computed(() => todos.items);

const projectOptions = computed(() => [
  { title: "All", value: "" },
  ...projects.items.map((p) => ({ title: p.name, value: p.id })),
]);

const statusOptions = [
  { title: "All", value: "" },
  { title: "OPEN", value: "OPEN" },
  { title: "IN_PROGRESS", value: "IN_PROGRESS" },
  { title: "DONE", value: "DONE" },
];
</script>

<template>
  <v-card class="pa-4" rounded="xl" elevation="1">
    <v-card-title class="d-flex align-center">
      <div class="text-h6">Todos</div>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="newTodo">
        New
      </v-btn>
    </v-card-title>

    <v-divider class="my-2" />

    <v-card-text>
      <!-- Filters -->
      <v-row class="mb-2" dense>
        <v-col cols="12" sm="6" md="4">
          <v-select
              v-model="filterProjectId"
              :items="projectOptions"
              item-title="title"
              item-value="value"
              label="Project"
              variant="outlined"
              density="comfortable"
          />
        </v-col>

        <v-col cols="12" sm="6" md="4">
          <v-select
              v-model="filterStatus"
              :items="statusOptions"
              item-title="title"
              item-value="value"
              label="Status"
              variant="outlined"
              density="comfortable"
          />
        </v-col>
      </v-row>

      <v-progress-linear v-if="todos.loading" indeterminate class="mb-4" />

      <v-alert
          v-if="!todos.loading && rows.length === 0"
          type="info"
          variant="tonal"
          title="No todos yet"
          text="Create your first todo."
      />

      <!-- Todo cards -->
      <v-row v-else dense>
        <v-col v-for="t in rows" :key="t.id" cols="12" md="6" lg="4">
          <v-card rounded="xl" elevation="1">
            <v-card-title class="d-flex align-start ga-2">
              <div class="text-subtitle-1 font-weight-bold">
                {{ t.title }}
              </div>
              <v-spacer />
              <v-btn icon="mdi-pencil" variant="text" @click="edit(t)" />
              <v-btn icon="mdi-delete" variant="text" color="error" @click="askDelete(t)" />
            </v-card-title>

            <v-card-text class="pt-0">
              <div v-if="t.description" class="text-body-2 text-medium-emphasis mb-3">
                {{ t.description }}
              </div>
              <div class="d-flex flex-wrap ga-2 mb-3">
                <v-chip size="small" label>{{ t.status }}</v-chip>
                <v-chip size="small" label>{{ t.priority }}</v-chip>
                <v-chip size="small" label>Project: {{ t.projectName }}</v-chip>
                <v-chip v-if="t.dueDate" size="small" label>Due: {{ t.dueDate }}</v-chip>
              </div>

              <div v-if="t.tags?.length" class="d-flex flex-wrap ga-2">
                <v-chip
                    v-for="tag in t.tags"
                    :key="tag.id"
                    size="small"
                    variant="tonal"
                    label
                >
                  #{{ tag.name }}
                </v-chip>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>

  <!-- Create/Edit dialog -->
  <TodoForm v-model="open" :todo="editing" @save="save" />

  <!-- Delete confirm dialog -->
  <v-dialog v-model="confirmOpen" max-width="520">
    <v-card rounded="xl">
      <v-card-title class="text-h6">Delete todo?</v-card-title>
      <v-card-text>
        This will delete:
        <div class="font-weight-medium mt-2">{{ toDelete?.title }}</div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="confirmOpen = false">Cancel</v-btn>
        <v-btn color="error" @click="confirmDelete">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
