<script setup lang="ts">
import { onMounted, ref } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import ProjectForm from "@/components/projects/ProjectForm.vue";
import { useProjectsStore } from "@/stores/projects";
import type { Project } from "@/api/projects";

const store = useProjectsStore();

const open = ref(false);
const editing = ref<Project | null>(null);

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
}

async function remove(p: Project) {
  if (!confirm(`Delete project "${p.name}"?`)) return;
  await store.remove(p.id);
}
</script>

<template>
  <div class="page">
    <div class="header">
      <h2>Projects</h2>
      <AppButton @click="newProject">New</AppButton>
    </div>

    <div v-if="store.loading" class="muted">Loading...</div>

    <div class="list">
      <div v-for="p in store.items" :key="p.id" class="row">
        <div class="main">
          <div class="title">{{ p.name }}</div>
          <div v-if="p.description" class="desc">{{ p.description }}</div>
        </div>
        <div class="actions">
          <AppButton variant="secondary" @click="edit(p)">Edit</AppButton>
          <AppButton variant="danger" @click="remove(p)">Delete</AppButton>
        </div>
      </div>
      <div v-if="!store.loading && store.items.length === 0" class="muted">
        No projects yet.
      </div>
    </div>

    <ProjectForm v-model="open" :project="editing" @save="save" />
  </div>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.list { display: grid; gap: 10px; }
.row { display: flex; justify-content: space-between; gap: 12px; padding: 12px; border: 1px solid #eee; border-radius: 14px; background: #fff; }
.main { display: grid; gap: 6px; }
.title { font-weight: 800; }
.desc { color: #555; font-size: 14px; }
.actions { display: flex; gap: 8px; align-items: center; }
.muted { color: #777; }
</style>
