import { defineStore } from "pinia";
import { ProjectsApi, type Project, type ProjectCreate } from "@/api/projects";

type ProjectsState = {
    items: Project[];
    loading: boolean;
};

export const useProjectsStore = defineStore("projects", {
    state: (): ProjectsState => ({
        items: [],
        loading: false,
    }),
    actions: {
        async load() {
            this.loading = true;
            try {
                this.items = await ProjectsApi.list();
                this.items.sort((a: Project, b: Project) =>
                    a.name.localeCompare(b.name)
                );
            } finally {
                this.loading = false;
            }
        },

        async create(body: ProjectCreate) {
            const created = await ProjectsApi.create(body);
            this.items.push(created);
            this.items.sort((a: Project, b: Project) =>
                a.name.localeCompare(b.name)
            );
            return created;
        },

        async update(id: string, body: ProjectCreate) {
            const updated = await ProjectsApi.update(id, body);
            const idx = this.items.findIndex((p: Project) => p.id === id);
            if (idx >= 0) this.items[idx] = updated;
            this.items.sort((a: Project, b: Project) =>
                a.name.localeCompare(b.name)
            );
            return updated;
        },

        async remove(id: string) {
            await ProjectsApi.remove(id);
            this.items = this.items.filter((p: Project) => p.id !== id);
        },
    },
});