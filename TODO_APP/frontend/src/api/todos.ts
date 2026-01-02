import { http } from "@/lib/http";

export type TodoStatus = "OPEN" | "IN_PROGRESS" | "DONE";
export type TodoPriority = "LOW" | "MEDIUM" | "HIGH";

export type TodoTagMini = {
    id: string;
    name: string;
};

export type Todo = {
    id: string;
    title: string;
    description?: string | null;
    status: TodoStatus;
    priority: TodoPriority;
    dueDate?: string | null; // LocalDate -> "YYYY-MM-DD"
    projectId: string;
    projectName: string;
    tags: TodoTagMini[];
    createdAt: string;
    updatedAt: string;
};

export type TodoCreate = {
    title: string;
    description?: string | null;
    status: TodoStatus;
    priority: TodoPriority;
    dueDate?: string | null;
    projectId: string;
    tagIds?: string[] | null;
};

export type TodoUpdate = TodoCreate;

export type TodoListParams = {
    projectId?: string;
    status?: TodoStatus;
};

export const TodosApi = {
    list: async (params?: TodoListParams) =>
        (
            await http.get<Todo[]>("/api/todos", {
                params: params ?? {},
            })
        ).data,

    get: async (id: string) => (await http.get<Todo>(`/api/todos/${id}`)).data,

    create: async (body: TodoCreate) =>
        (await http.post<Todo>("/api/todos", body)).data,

    update: async (id: string, body: TodoUpdate) =>
        (await http.put<Todo>(`/api/todos/${id}`, body)).data,

    remove: async (id: string) => {
        await http.delete(`/api/todos/${id}`);
    },
};
