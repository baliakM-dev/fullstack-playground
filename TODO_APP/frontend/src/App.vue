<script setup lang="ts"></script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">TODO</div>
      <nav class="nav">
        <RouterLink to="/todos" class="link" active-class="active">Todos</RouterLink>
        <RouterLink to="/projects" class="link" active-class="active">Projects</RouterLink>
        <RouterLink to="/tags" class="link" active-class="active">Tags</RouterLink>
      </nav>
      <div class="hint">
        Backend: <a href="http://localhost:8080/swagger-ui" target="_blank">Swagger</a>
      </div>
    </aside>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style>
body { margin: 0; background: #fafafa; color: #111; font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Arial; }
a { color: inherit; }
.layout { display: grid; grid-template-columns: 240px 1fr; min-height: 100vh; }
.sidebar { border-right: 1px solid #eee; background: #fff; padding: 16px; display: grid; gap: 14px; align-content: start; }
.brand { font-weight: 900; font-size: 18px; letter-spacing: 0.5px; }
.nav { display: grid; gap: 8px; }
.link { text-decoration: none; padding: 10px 12px; border-radius: 12px; border: 1px solid transparent; }
.link:hover { background: #f3f3f3; }
.active { background: #111; color: #fff; }
.content { padding: 18px; }
.hint { font-size: 12px; color: #666; }
@media (max-width: 820px) {
  .layout { grid-template-columns: 1fr; }
  .sidebar { border-right: none; border-bottom: 1px solid #eee; grid-auto-flow: column; align-items: center; }
  .nav { grid-auto-flow: column; }
}
</style>
