import axios from "axios";
import { config } from "../../app/config";

/**
 * Raw HTTP client shared by the whole app: base URL, auth token interceptor,
 * normalized error handling (unwraps the backend's ApiResponse error body).
 */
const apiClient = axios.create({
  baseURL: config.apiBaseUrl,
  headers: { "Content-Type": "application/json" },
});

apiClient.interceptors.request.use((requestConfig) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    requestConfig.headers.Authorization = `Bearer ${token}`;
  }
  return requestConfig;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const body = error.response?.data;
    const message = body?.message || error.message || "Đã có lỗi xảy ra, vui lòng thử lại";
    const normalizedError = new Error(message);
    normalizedError.status = error.response?.status;
    normalizedError.fieldErrors = body?.errors ?? [];
    return Promise.reject(normalizedError);
  }
);

export default apiClient;
