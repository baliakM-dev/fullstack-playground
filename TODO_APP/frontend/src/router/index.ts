import { createRouter, createWebHistory } from "vue-router";
import TodosView from "@/views/TodosView.vue";
import ProjectsView from "@/views/ProjectsView.vue";
import TagsView from "@/views/TagsView.vue";

export default createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: "/", redirect: "/todos" },
        { path: "/todos", component: TodosView, meta: { title: "Todos" } },
        { path: "/projects", component: ProjectsView, meta: { title: "Projects" } },
        { path: "/tags", component: TagsView, meta: { title: "Tags" } },
    ],
});
