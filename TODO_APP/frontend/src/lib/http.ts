import axios from "axios";

export const http = axios.create({
    baseURL: "/",
    headers: { "Content-Type": "application/json" },
});

http.interceptors.response.use(
    (res) => res,
    (err) => {
        const status = err?.response?.status;
        const data = err?.response?.data;

        // Backend posiela ApiError { message, violations... }
        const message =
            data?.message ||
            (Array.isArray(data?.violations) && data.violations.length
                ? data.violations.map((v: any) => `${v.field}: ${v.message}`).join("\n")
                : err.message);

        console.error("API error:", { status, data, err });
        alert(`API error ${status ?? ""}\n${message}`);

        return Promise.reject(err);
    }
);
