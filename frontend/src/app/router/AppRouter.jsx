// NOTE: requires react-router-dom -> npm install react-router-dom
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AppLayout from "../layout/AppLayout";

/**
 * Root router: wires each feature's pages into a route. Import each
 * feature's pages here as they are built, e.g.:
 *   import { LoginPage } from "../../features/authentication_and_user_account";
 */
export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          {/* TODO: add routes per feature, e.g.
          <Route path="/login" element={<LoginPage />} />
          <Route path="/orders" element={<OrderListPage />} />
          */}
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
