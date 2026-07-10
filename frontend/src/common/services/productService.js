/**
 * Business-logic wrappers around the raw HTTP client in ../api, shared by
 * 2+ features (e.g. a generic notificationService). Feature-specific
 * services (orderService, paymentService, ...) should live inside that
 * feature's own folder instead.
 */
// common/services/productService.js
export const getProducts = async () => {
  const res = await fetch(
    "https://dummyjson.com/products?limit=100"
  );

  const data = await res.json();

  return data.products;
};