import { defineStore } from "pinia";
import {
    TodosApi,
    type Todo,
    type TodoCreate,
    type TodoUpdate,
    type TodoListParams,
    type TodoStatus,
} from "@/api/todos";

type TodosFilter = {
    projectId: string;
    status: "" | TodoStatus;
};

type TodosState = {
    items: Todo[];
    loading: boolean;
    filter: TodosFilter;
};

export const useTodosStore = defineStore("todos", {
    state: (): TodosState => ({
        items: [],
        loading: false,
        filter: {
            projectId: "",
            status: "",
        },
    }),
    actions: {
        async load(params?: TodoListParams) {
            this.loading = true;
            try {
                this.items = await TodosApi.list(params);
                // newest first
                this.items.sort((a: Todo, b: Todo) => b.createdAt.localeCompare(a.createdAt));
            } finally {
                this.loading = false;
            }
        },

        async create(body: TodoCreate) {
            const created = await TodosApi.create(body);
            this.items.unshift(created);
            return created;
        },

        async update(id: string, body: TodoUpdate) {
            const updated = await TodosApi.update(id, body);
            const idx = this.items.findIndex((t: Todo) => t.id === id);
            if (idx >= 0) this.items[idx] = updated;
            return updated;
        },

        async remove(id: string) {
            await TodosApi.remove(id);
            this.items = this.items.filter((t: Todo) => t.id !== id);
        },
    },
});
