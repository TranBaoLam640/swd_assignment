import apiClient from "../../../common/api";

/** Calls GET /api/v1/categories (public) — feeds the category filter menu (UC09). */
export async function listCategories() {
  const { data } = await apiClient.get("/categories");
  return data.data;
}
