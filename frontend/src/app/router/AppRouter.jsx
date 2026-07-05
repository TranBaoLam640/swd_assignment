import { BrowserRouter, Routes, Route } from "react-router-dom";
import AppLayout from "../layout/AppLayout";
import { LoginPage, RegisterPage } from "../../features/authentication_and_user_account";

/**
 * Root router: wires each feature's pages into a route. Import each
 * feature's pages here as they are built.
 */
export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
