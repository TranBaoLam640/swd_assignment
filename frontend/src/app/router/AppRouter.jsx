import { BrowserRouter, Routes, Route } from "react-router-dom";
import AppLayout from "../layout/AppLayout";
import RequireRole from "./RequireRole";
import { LoginPage, RegisterPage } from "../../features/authentication_and_user_account";
import { ShoppingCartPage } from "../../features/cart_management_module";
import {
  ProductListPage,
  ProductDetailPage,
  ManagerProductListPage,
  ManagerProductFormPage,
} from "../../features/product_management_module";
import {
  CheckoutPage,
  OrderHistoryPage,
  OrderDetailPage,
} from "../../features/order_management_module";
import { AddressListPage } from "../../features/address_management_module";

/**
 * Root router: wires each feature's pages into a route. Import each
 * feature's pages here as they are built. Manager-only and customer-only
 * routes are wrapped in RequireRole to mirror SecurityConfig's
 * /api/v1/manager/** and /api/v1/customer/** rules.
 */
export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<ProductListPage />} />
          <Route path="/products/:productId" element={<ProductDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/cart"
            element={
              <RequireRole roles={["CUSTOMER"]}>
                <ShoppingCartPage />
              </RequireRole>
            }
          />
          <Route
            path="/checkout"
            element={
              <RequireRole roles={["CUSTOMER"]}>
                <CheckoutPage />
              </RequireRole>
            }
          />
          <Route
            path="/orders"
            element={
              <RequireRole roles={["CUSTOMER"]}>
                <OrderHistoryPage />
              </RequireRole>
            }
          />
          <Route
            path="/orders/:orderId"
            element={
              <RequireRole roles={["CUSTOMER"]}>
                <OrderDetailPage />
              </RequireRole>
            }
          />
          <Route
            path="/addresses"
            element={
              <RequireRole roles={["CUSTOMER"]}>
                <AddressListPage />
              </RequireRole>
            }
          />
          <Route
            path="/manager/products"
            element={
              <RequireRole roles={["MANAGER"]}>
                <ManagerProductListPage />
              </RequireRole>
            }
          />
          <Route
            path="/manager/products/new"
            element={
              <RequireRole roles={["MANAGER"]}>
                <ManagerProductFormPage />
              </RequireRole>
            }
          />
          <Route
            path="/manager/products/:productId/edit"
            element={
              <RequireRole roles={["MANAGER"]}>
                <ManagerProductFormPage />
              </RequireRole>
            }
          />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
