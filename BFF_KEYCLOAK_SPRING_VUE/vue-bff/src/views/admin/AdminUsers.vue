<template>
  <h1>Admin users</h1>
  <p>Sem pôjde správa používateľov.</p>
  <section>
    <h1>Admin users</h1>
    <p>Údaje sa načítajú priamo z BFF <code>/api/admin/users</code>.</p>

    <div v-if="users.status === 'loading'">Načítavam zoznam…</div>

    <ul v-else-if="users.status === 'success'">
      <li v-for="user in users.data" :key="user.username">
        <strong>{{ user.fullName }}</strong>
        <span class="muted">({{ user.username }} – {{ user.department }})</span>
        <span class="roles">{{ user.roles.join(", ") }}</span>
      </li>
    </ul>

    <div v-else-if="users.status === 'unauthorized'">
      <p class="error">401 – najprv sa prihlás, údaje sa nebudú cacheovať v UI.</p>
    </div>

    <div v-else-if="users.status === 'forbidden'">
      <p class="error">403 – BFF zamietol prístup bez roly <strong>admin</strong>.</p>
    </div>

    <div v-else>
      <p class="error">Chyba z BFF: {{ users.message ?? "neznámy dôvod" }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchAdminUsers, type AdminUserSummary, type ApiLoadState } from "../../lib/protectedApi";

const users = ref<ApiLoadState<AdminUserSummary[]>>({ status: "loading" });

const load = async () => {
  users.value = { status: "loading" };
  users.value = await fetchAdminUsers();
};

onMounted(load);
</script>

<style scoped>
.muted {
  color: #5f6368;
  font-size: 0.9rem;
}
.roles {
  display: inline-block;
  margin-left: 6px;
  color: #1b5e20;
  font-weight: 600;
}
.error {
  color: #c62828;
  font-weight: 600;
}
li {
  margin-bottom: 8px;
}
</style>