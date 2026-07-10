// NOTE: requires react-router-dom -> npm install react-router-dom
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AppLayout from "../layout/AppLayout";
import ProductListPage from "../../features/order_management_module/ProductListPage";

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
            <Route
                path="/"
                element={<ProductListPage/>}
            />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
