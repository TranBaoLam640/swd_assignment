/**
 * Business-logic wrappers around the raw HTTP client in ../api, shared by
 * 2+ features (e.g. a generic notificationService). Feature-specific
 * services (orderService, paymentService, ...) should live inside that
 * feature's own folder instead.
 *
 * Product catalog calls (browse/get) live in
 * `features/product_management_module/services/productService.js` — both
 * the customer storefront pages and the manager catalog pages import from
 * there directly, so this file stays a placeholder.
 */
