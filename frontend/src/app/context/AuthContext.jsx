import { createContext, useCallback, useContext, useState } from "react";

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    const raw = localStorage.getItem("userInfo");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

/**
 * Global auth state (current user + JWT access token), backed by
 * localStorage so a page refresh doesn't log the user out. LoginPage calls
 * login() after a successful POST /api/v1/auth/login; Header calls
 * logout(). Any component reads the current user/role via useAuth()
 * instead of touching localStorage directly.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const [accessToken, setAccessToken] = useState(() => localStorage.getItem("accessToken"));

  const login = useCallback((token, userInfo) => {
    localStorage.setItem("accessToken", token);
    localStorage.setItem("userInfo", JSON.stringify(userInfo));
    setAccessToken(token);
    setUser(userInfo);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userInfo");
    setAccessToken(null);
    setUser(null);
  }, []);

  const value = {
    user,
    accessToken,
    isAuthenticated: Boolean(accessToken),
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside <AuthProvider>");
  }
  return ctx;
}
