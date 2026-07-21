import apiClient from "../../../common/api";

/** Calls POST /api/v1/customer/orders/checkout. addressId is now required —
 * see AddressSelector on CheckoutPage (Manage Shipping Address requirement:
 * a customer must have and pick a shipping address before placing an order). */
export async function checkout({ paymentMethod, addressId }) {
  const { data } = await apiClient.post("/customer/orders/checkout", { paymentMethod, addressId });
  return data.data;
}

/** Calls GET /api/v1/customer/orders (order history, newest first). */
export async function listMyOrders() {
  const { data } = await apiClient.get("/customer/orders");
  return data.data;
}

/** Calls GET /api/v1/customer/orders/{orderId}. */
export async function getOrder(orderId) {
  const { data } = await apiClient.get(`/customer/orders/${orderId}`);
  return data.data;
}

/** Calls PUT /api/v1/customer/orders/{orderId}/cancel. */
export async function cancelOrder(orderId, reason) {
  const { data } = await apiClient.put(`/customer/orders/${orderId}/cancel`, { reason });
  return data.data;
}

/**
 * Calls POST /api/v1/customer/orders/{orderId}/payment-url (Payment
 * module) — only valid while the order is PENDING_PAYMENT. Returns the
 * VNPAY hosted-checkout URL to redirect the browser to.
 */
export async function createPaymentUrl(orderId) {
  const { data } = await apiClient.post(`/customer/orders/${orderId}/payment-url`);
  return data.data;
}
