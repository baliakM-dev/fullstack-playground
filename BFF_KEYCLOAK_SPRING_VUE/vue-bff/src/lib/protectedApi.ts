import { api } from "./api";

export type ApiLoadState<T> = {
    status: "loading" | "success" | "unauthorized" | "forbidden" | "error";
    data?: T;
    statusCode?: number;
    message?: string;
};

export type AdminUserSummary = {
    username: string;
    fullName: string;
    roles: string[];
    department: string;
};

export type HrSummary = {
    message: string;
    activeEmployees: number;
    openPositions: number;
    payrollClosed: boolean;
};

const mapError = <T>(error: any): ApiLoadState<T> => {
    const status = error?.response?.status;

    if (status === 401) {
        return { status: "unauthorized", statusCode: status };
    }

    if (status === 403) {
        return { status: "forbidden", statusCode: status };
    }

    return {
        status: "error",
        statusCode: status,
        message: error?.message ?? "Unexpected error while contacting the BFF",
    };
};

const safeGet = async <T>(url: string): Promise<ApiLoadState<T>> => {
    try {
        const { data } = await api.get<T>(url);
        return { status: "success", data };
    } catch (error: any) {
        return mapError<T>(error);
    }
};
export const fetchAdminUsers = () => safeGet<AdminUserSummary[]>("/api/admin/users");
export const fetchHrSummary = () => safeGet<HrSummary>("/api/hr/summary");