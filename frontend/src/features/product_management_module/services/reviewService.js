import apiClient from "../../../common/api";

/** Calls GET /api/v1/products/{productId}/reviews. */
export async function listProductReviews(productId) {
  const { data } = await apiClient.get(`/products/${productId}/reviews`);
  return data.data;
}

/** Calls GET /api/v1/customer/reviews?orderId=... */
export async function listMyOrderReviews(orderId) {
  const { data } = await apiClient.get("/customer/reviews", { params: { orderId } });
  return data.data;
}

/** Calls POST /api/v1/customer/reviews. */
export async function createReview(payload) {
  const { data } = await apiClient.post("/customer/reviews", payload);
  return data.data;
}
