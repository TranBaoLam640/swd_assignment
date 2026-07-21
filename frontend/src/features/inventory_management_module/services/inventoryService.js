import apiClient from "../../../common/api";

/** Calls GET /api/v1/manager/inventory/{productId}. */
export async function getStock(productId) {
  const { data } = await apiClient.get(`/manager/inventory/${productId}`);
  return data.data;
}

/** Calls POST /api/v1/manager/inventory/{productId}/increase. */
export async function increaseStock(productId, quantity) {
  const { data } = await apiClient.post(`/manager/inventory/${productId}/increase`, { quantity });
  return data.data;
}

/** Calls POST /api/v1/manager/inventory/{productId}/decrease. */
export async function decreaseStock(productId, quantity) {
  const { data } = await apiClient.post(`/manager/inventory/${productId}/decrease`, { quantity });
  return data.data;
}

/** Calls PUT /api/v1/manager/inventory/{productId} (sets an absolute value). */
export async function setStock(productId, quantity) {
  const { data } = await apiClient.put(`/manager/inventory/${productId}`, { quantity });
  return data.data;
}
