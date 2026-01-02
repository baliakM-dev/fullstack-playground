<script setup lang="ts">
import { onMounted, ref } from "vue";
import AppButton from "@/components/ui/AppButton.vue";
import TagForm from "@/components/tags/TagForm.vue";
import { useTagsStore } from "@/stores/tags";
import type { Tag } from "@/api/tags";

const store = useTagsStore();

const open = ref(false);
const editing = ref<Tag | null>(null);

onMounted(async () => {
  if (store.items.length === 0) await store.load();
});

function newTag() {
  editing.value = null;
  open.value = true;
}

function edit(t: Tag) {
  editing.value = t;
  open.value = true;
}

async function save(payload: { name: string }) {
  if (editing.value) await store.update(editing.value.id, payload);
  else await store.create(payload);
}

async function remove(t: Tag) {
  if (!confirm(`Delete tag "${t.name}"?`)) return;
  await store.remove(t.id);
}
</script>

<template>
  <div class="page">
    <div class="header">
      <h2>Tags</h2>
      <AppButton @click="newTag">New</AppButton>
    </div>

    <div v-if="store.loading" class="muted">Loading...</div>

    <div class="list">
      <div v-for="t in store.items" :key="t.id" class="row">
        <div class="title">{{ t.name }}</div>
        <div class="actions">
          <AppButton variant="secondary" @click="edit(t)">Edit</AppButton>
          <AppButton variant="danger" @click="remove(t)">Delete</AppButton>
        </div>
      </div>

      <div v-if="!store.loading && store.items.length === 0" class="muted">
        No tags yet.
      </div>
    </div>

    <TagForm v-model="open" :tag="editing" @save="save" />
  </div>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.list { display: grid; gap: 10px; }
.row { display: flex; justify-content: space-between; gap: 12px; padding: 12px; border: 1px solid #eee; border-radius: 14px; background: #fff; }
.title { font-weight: 800; }
.actions { display: flex; gap: 8px; align-items: center; }
.muted { color: #777; }
</style>
