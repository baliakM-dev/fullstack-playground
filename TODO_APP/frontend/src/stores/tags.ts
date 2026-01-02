import { defineStore } from "pinia";
import { TagsApi, type Tag, type TagUpsert } from "@/api/tags";

type TagsState = {
    items: Tag[];
    loading: boolean;
};

export const useTagsStore = defineStore("tags", {
    state: (): TagsState => ({
        items: [],
        loading: false,
    }),
    actions: {
        async load() {
            this.loading = true;
            try {
                this.items = await TagsApi.list();
                this.items.sort((a: Tag, b: Tag) => a.name.localeCompare(b.name));
            } finally {
                this.loading = false;
            }
        },

        async create(body: TagUpsert) {
            const created = await TagsApi.create(body);
            this.items.push(created);
            this.items.sort((a: Tag, b: Tag) => a.name.localeCompare(b.name));
            return created;
        },

        async update(id: string, body: TagUpsert) {
            const updated = await TagsApi.update(id, body);
            const idx = this.items.findIndex((t: Tag) => t.id === id);
            if (idx >= 0) this.items[idx] = updated;
            this.items.sort((a: Tag, b: Tag) => a.name.localeCompare(b.name));
            return updated;
        },

        async remove(id: string) {
            await TagsApi.remove(id);
            this.items = this.items.filter((t: Tag) => t.id !== id);
        },
    },
});
