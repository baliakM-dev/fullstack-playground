<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import ProjectForm from "@/components/projects/ProjectForm.vue";
import { useProjectsStore } from "@/stores/projects";
import type { Project } from "@/api/projects";

const store = useProjectsStore();

const open = ref(false);
const editing = ref<Project | null>(null);

const confirmOpen = ref(false);
const toDelete = ref<Project | null>(null);

const empty = computed(() => !store.loading && store.items.length === 0);

onMounted(async () => {
  if (store.items.length === 0) await store.load();
});

function newProject() {
  editing.value = null;
  open.value = true;
}

function edit(p: Project) {
  editing.value = p;
  open.value = true;
}

async function save(payload: { name: string; description?: string | null }) {
  if (editing.value) await store.update(editing.value.id, payload);
  else await store.create(payload);
  open.value = false;
}

function askDelete(p: Project) {
  toDelete.value = p;
  confirmOpen.value = true;
}

async function confirmDelete() {
  if (!toDelete.value) return;
  await store.remove(toDelete.value.id);
  confirmOpen.value = false;
  toDelete.value = null;
}
</script>

<template>
  <v-card class="pa-4" rounded="xl" elevation="1">
    <v-card-title class="d-flex align-center">
      <div class="text-h6">Projects</div>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="newProject">
        New
      </v-btn>
    </v-card-title>

    <v-divider class="my-2" />

    <v-card-text>
      <v-progress-linear v-if="store.loading" indeterminate class="mb-4" />

      <v-alert
          v-if="empty"
          type="info"
          variant="tonal"
          title="No projects yet"
          text="Create your first project to group your todos."
      />

      <v-list v-else lines="two">
        <v-list-item v-for="p in store.items" :key="p.id" :title="p.name">
          <template #subtitle>
            <span v-if="p.description">{{ p.description }}</span>
            <span v-else class="text-medium-emphasis">No description</span>
          </template>

          <template #append>
            <div class="d-flex ga-2">
              <v-btn
                  variant="tonal"
                  color="primary"
                  size="small"
                  prepend-icon="mdi-pencil"
                  @click="edit(p)"
              >
                Edit
              </v-btn>

              <v-btn
                  variant="tonal"
                  color="error"
                  size="small"
                  prepend-icon="mdi-delete"
                  @click="askDelete(p)"
              >
                Delete
              </v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>

  <!-- Create/Edit dialog (tvoj ProjectForm) -->
  <ProjectForm v-model="open" :project="editing" @save="save" />

  <!-- Delete confirm dialog -->
  <v-dialog v-model="confirmOpen" max-width="520">
    <v-card rounded="xl">
      <v-card-title class="text-h6">Delete project?</v-card-title>
      <v-card-text>
        This will delete:
        <div class="font-weight-medium mt-2">
          {{ toDelete?.name }}
        </div>
        <div class="text-medium-emphasis mt-2">
          If the project still has todos, backend should return 409 and we’ll show an error.
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="confirmOpen = false">Cancel</v-btn>
        <v-btn color="error" @click="confirmDelete">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
