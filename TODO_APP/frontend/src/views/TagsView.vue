<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import TagForm from "@/components/tags/TagForm.vue";
import { useTagsStore } from "@/stores/tags";
import type { Tag } from "@/api/tags";

const store = useTagsStore();

const open = ref(false);
const editing = ref<Tag | null>(null);

const confirmOpen = ref(false);
const toDelete = ref<Tag | null>(null);

const empty = computed(() => !store.loading && store.items.length === 0);

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
  open.value = false;
}

function askDelete(t: Tag) {
  toDelete.value = t;
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
      <div class="text-h6">Tags</div>
      <v-spacer />
      <v-btn color="primary" prepend-icon="mdi-plus" @click="newTag">
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
          title="No tags yet"
          text="Create tags and attach them to todos."
      />

      <v-list v-else>
        <v-list-item v-for="t in store.items" :key="t.id" :title="t.name">
          <template #append>
            <div class="d-flex ga-2">
              <v-btn
                  variant="tonal"
                  color="primary"
                  size="small"
                  prepend-icon="mdi-pencil"
                  @click="edit(t)"
              >
                Edit
              </v-btn>

              <v-btn
                  variant="tonal"
                  color="error"
                  size="small"
                  prepend-icon="mdi-delete"
                  @click="askDelete(t)"
              >
                Delete
              </v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>

  <!-- Create/Edit dialog -->
  <TagForm v-model="open" :tag="editing" @save="save" />

  <!-- Delete confirm dialog -->
  <v-dialog v-model="confirmOpen" max-width="520">
    <v-card rounded="xl">
      <v-card-title class="text-h6">Delete tag?</v-card-title>

      <v-card-text>
        This will delete:
        <div class="font-weight-medium mt-2">{{ toDelete?.name }}</div>
        <div class="text-medium-emphasis mt-2">
          If the tag is used by any todo, backend may return 409.
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
