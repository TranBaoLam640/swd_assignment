import { Navigate } from "react-router-dom";
import { useAuth } from "../context";

/**
 * Route guard: redirects to /login if not authenticated, or to / if
 * authenticated but without one of the allowed roles. Mirrors
 * SecurityConfig's role-based path-prefix rules (e.g. /api/v1/manager/**
 * -> MANAGER) on the frontend, so the UI never lets a user click into a
 * page whose API calls would just 403.
 */
export default function RequireRole({ roles, children }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  if (roles && !roles.includes(user?.role)) {
    return <Navigate to="/" replace />;
  }
  return children;
}
