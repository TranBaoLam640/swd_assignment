import apiClient from "../../../common/api";

/** Manager shop lookup: calls GET /api/v1/manager/shops. */
export async function listShopsForManager() {
  const { data } = await apiClient.get("/manager/shops");
  return data.data;
}

/** Calls POST /api/v1/manager/shops. */
export async function createShop(payload) {
  const { data } = await apiClient.post("/manager/shops", payload);
  return data.data;
}
