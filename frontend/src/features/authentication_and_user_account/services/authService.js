import apiClient from "../../../common/api";

/**
 * Calls POST /api/v1/auth/login. Returns the UserResponse payload
 * ({ accessToken, userInfo }) unwrapped from the ApiResponse envelope.
 */
export async function login({ email, password }) {
  const { data } = await apiClient.post("/auth/login", { email, password });
  return data.data;
}

/**
 * Calls POST /api/v1/auth/register. Returns the full ApiResponse envelope
 * (data.data is null on success — register only returns a message).
 *
 * `role` is one of "CUSTOMER" | "MANAGER" | "SHIPPER" — ADMIN is not
 * selectable here, the backend rejects it (see AuthServiceImpl.register()).
 *
 * `confirmPassword` must match `password` — checked client-side in
 * RegisterPage and again server-side (PasswordMismatchException).
 */
export async function register({ email, password, confirmPassword, fullName, phoneNumber, role }) {
  const { data } = await apiClient.post("/auth/register", {
    email,
    password,
    confirmPassword,
    fullName,
    phoneNumber,
    role,
  });
  return data;
}
