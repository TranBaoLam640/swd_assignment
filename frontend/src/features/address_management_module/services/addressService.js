import apiClient from "../../../common/api";

/**
 * UC "Manage Shipping Address". Backend: AddressController under
 * /api/v1/customer/addresses (same /customer/** role-protected prefix as
 * Cart/Order). The "defaultAddress" field name (not "isDefault") matches
 * the backend DTO exactly — Lombok's boolean-getter naming would otherwise
 * make the JSON property "default" instead of "isDefault", which is a
 * common source of silent bugs.
 */

/** GET /api/v1/customer/addresses — default address first, then oldest-first. */
export async function listAddresses() {
  const { data } = await apiClient.get("/customer/addresses");
  return data.data;
}

/** POST /api/v1/customer/addresses. The very first address a customer saves
 * is always forced default by the backend regardless of what's sent here. */
export async function createAddress({
  receiverName,
  receiverPhone,
  province,
  district,
  ward,
  specificAddress,
  defaultAddress,
}) {
  const { data } = await apiClient.post("/customer/addresses", {
    receiverName,
    receiverPhone,
    province,
    district,
    ward,
    specificAddress,
    defaultAddress,
  });
  return data.data;
}

/** PUT /api/v1/customer/addresses/{addressId}. */
export async function updateAddress(addressId, payload) {
  const { data } = await apiClient.put(`/customer/addresses/${addressId}`, payload);
  return data.data;
}

/** PATCH /api/v1/customer/addresses/{addressId}/default — unsets every other
 * address of this customer and makes this one the default. */
export async function setDefaultAddress(addressId) {
  const { data } = await apiClient.patch(`/customer/addresses/${addressId}/default`);
  return data.data;
}

/** DELETE /api/v1/customer/addresses/{addressId}. */
export async function deleteAddress(addressId) {
  await apiClient.delete(`/customer/addresses/${addressId}`);
}
