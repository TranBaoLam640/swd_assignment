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
 */
export async function register({ email, password, fullName, phoneNumber }) {
  const { data } = await apiClient.post("/auth/register", {
    email,
    password,
    fullName,
    phoneNumber,
  });
  return data;
}
