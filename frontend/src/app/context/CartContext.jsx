import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { viewCart } from "../../features/cart_management_module/services/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

/**
 * Tracks the current customer's cart item count so the Header badge stays
 * in sync without every page re-fetching the whole cart. Only CUSTOMER
 * accounts have a cart (SecurityConfig restricts /api/v1/customer/** to
 * role CUSTOMER), so this silently no-ops (count 0) for guests, managers,
 * admins and shippers. Call refreshCartCount() after any add/update/remove
 * so the badge updates immediately instead of waiting for a reload.
 */
export function CartProvider({ children }) {
  const { user, isAuthenticated } = useAuth();
  const [cartCount, setCartCount] = useState(0);

  const refreshCartCount = useCallback(async () => {
    if (!isAuthenticated || user?.role !== "CUSTOMER") {
      setCartCount(0);
      return;
    }
    try {
      const items = await viewCart();
      setCartCount((items ?? []).reduce((sum, item) => sum + item.quantity, 0));
    } catch {
      setCartCount(0);
    }
  }, [isAuthenticated, user]);

  useEffect(() => {
    refreshCartCount();
  }, [refreshCartCount]);

  return (
    <CartContext.Provider value={{ cartCount, refreshCartCount }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) {
    throw new Error("useCart must be used inside <CartProvider>");
  }
  return ctx;
}
