import apiClient from "../../../common/api";

/**
 * Calls POST /api/v1/customer/cart. Returns the CartItemResponse payload
 * unwrapped from the ApiResponse envelope.
 */
export async function addToCart({ productId, quantity }) {
  const { data } = await apiClient.post("/customer/cart", { productId, quantity });
  return data.data;
}

/** Calls GET /api/v1/customer/cart. Returns the list of CartItemResponse. */
export async function viewCart() {
  const { data } = await apiClient.get("/customer/cart");
  return data.data;
}

/**
 * Calls PUT /api/v1/customer/cart/{cartItemId}. Per UC13's alternative
 * flow, quantity 0 forwards to Remove Item on the backend — data.data is
 * null in that case.
 */
export async function updateCartItemQuantity(cartItemId, quantity) {
  const { data } = await apiClient.put(`/customer/cart/${cartItemId}`, { quantity });
  return data.data;
}

/** Calls DELETE /api/v1/customer/cart/{cartItemId}. */
export async function removeCartItem(cartItemId) {
  const { data } = await apiClient.delete(`/customer/cart/${cartItemId}`);
  return data.data;
}
