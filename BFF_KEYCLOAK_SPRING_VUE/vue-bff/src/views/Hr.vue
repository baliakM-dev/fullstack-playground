<template>
  <div>
    <h1>HR sekcia</h1>
    <p>Viditeľné len pre rolu <strong>hr</strong></p>

    <div v-if="summary.status === 'loading'">Načítavam HR dáta z BFF…</div>

    <div v-else-if="summary.status === 'success'">
      <p>{{ summary.data?.message }}</p>
      <ul>
        <li>Aktívnych zamestnancov: {{ summary.data?.activeEmployees }}</li>
        <li>Otvorené pozície: {{ summary.data?.openPositions }}</li>
        <li>Payroll uzavretý: {{ summary.data?.payrollClosed ? "Áno" : "Nie" }}</li>
      </ul>
    </div>

    <div v-else-if="summary.status === 'unauthorized'">
      <p class="error">401 – BFF hovorí, že nemáš session.</p>
    </div>

    <div v-else-if="summary.status === 'forbidden'">
      <p class="error">403 – BFF zamietol prístup bez roly <strong>hr</strong> alebo <strong>admin</strong>.</p>
    </div>

    <div v-else>
      <p class="error">Chyba pri volaní BFF: {{ summary.message ?? "neznáma chyba" }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchHrSummary, type ApiLoadState, type HrSummary } from "../lib/protectedApi";

const summary = ref<ApiLoadState<HrSummary>>({ status: "loading" });

const load = async () => {
  summary.value = { status: "loading" };
  summary.value = await fetchHrSummary();
};

onMounted(load);
</script>

<style scoped>
.error {
  color: #c62828;
  font-weight: 600;
}
</style>