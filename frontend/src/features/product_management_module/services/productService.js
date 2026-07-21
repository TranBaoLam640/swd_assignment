import apiClient from "../../../common/api";

/**
 * Calls GET /api/v1/products (public, active products only).
 *
 * `keyword`/`categoryId` are optional — UC09 (Browse & Search Product).
 * Omit both (or call with no args) to get the full active catalog, same
 * as before search/filter existed.
 */
export async function browseProducts({ keyword, categoryId } = {}) {
  const params = {};
  if (keyword) params.keyword = keyword;
  if (categoryId) params.categoryId = categoryId;

  const { data } = await apiClient.get("/products", { params });
  return data.data;
}

/** Calls GET /api/v1/products/{productId}. */
export async function getProduct(productId) {
  const { data } = await apiClient.get(`/products/${productId}`);
  return data.data;
}

/** Manager catalog view: calls GET /api/v1/manager/products (includes inactive products). */
export async function listAllProductsForManager() {
  const { data } = await apiClient.get("/manager/products");
  return data.data;
}

/** Calls POST /api/v1/manager/products. */
export async function createProduct(payload) {
  const { data } = await apiClient.post("/manager/products", payload);
  return data.data;
}

/** Calls PUT /api/v1/manager/products/{productId}. */
export async function updateProduct(productId, payload) {
  const { data } = await apiClient.put(`/manager/products/${productId}`, payload);
  return data.data;
}
