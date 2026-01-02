import { http } from "@/lib/http";

export type Tag = {
    id: string;
    name: string;
    createdAt: string;
    updatedAt: string;
};

export type TagUpsert = {
    name: string;
};

export const TagsApi = {
    list: async () => (await http.get<Tag[]>("/api/tags")).data,

    get: async (id: string) => (await http.get<Tag>(`/api/tags/${id}`)).data,

    create: async (body: TagUpsert) =>
        (await http.post<Tag>("/api/tags", body)).data,

    update: async (id: string, body: TagUpsert) =>
        (await http.put<Tag>(`/api/tags/${id}`, body)).data,

    remove: async (id: string) => {
        await http.delete(`/api/tags/${id}`);
    },
};
