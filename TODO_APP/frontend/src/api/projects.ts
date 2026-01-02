import { http } from "@/lib/http";

export type Project = {
    id: string;
    name: string;
    description?: string | null;
    createdAt: string;
    updatedAt: string;
};

export type ProjectCreate = { name: string; description?: string | null };

export const ProjectsApi = {
    list: async () => (await http.get<Project[]>("/api/projects")).data,
    create: async (body: ProjectCreate) =>
        (await http.post<Project>("/api/projects", body)).data,
    update: async (id: string, body: ProjectCreate) =>
        (await http.put<Project>(`/api/projects/${id}`, body)).data,
    remove: async (id: string) => {
        await http.delete(`/api/projects/${id}`);
    },
};
