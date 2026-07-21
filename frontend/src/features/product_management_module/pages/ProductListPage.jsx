import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Container, Row, Col, Card, Button, Alert, Spinner, Badge, Form, InputGroup } from "react-bootstrap";
import { browseProducts } from "../services/productService";
import { listCategories } from "../services/categoryService";
import {
  addToCart,
  viewCart,
  updateCartItemQuantity,
  removeCartItem,
} from "../../cart_management_module/services/cartService";
import { useAuth, useCart } from "../../../app/context";

/**
 * Customer storefront / home page — UC09 (Browse & Search Product) + UC11
 * (Browse Products, add to cart). GET /api/v1/products only ever returns
 * active products (see ProductServiceImpl#browseActiveProducts), so nothing
 * here needs to filter isActive itself.
 *
 * Search (keyword) and category filter both call the same endpoint with
 * optional query params — see UC09 main sequence steps 2-6.
 *
 * Cart quantity per product: a card shows the plain "Thêm vào giỏ" button
 * only while quantity in cart is 0. Once it's > 0, that button is replaced
 * by a "−"/"+" stepper (quantity in the middle) — no more text button once
 * the product is already in the cart, so both controls read as one
 * consistent stepper instead of a button + a different-looking button.
 * Every +1 (and every manual quantity increase) is checked against
 * product.stockQuantity client-side — the backend (CartServiceImpl)
 * re-checks the same thing, so this is UX, not the only line of defense.
 */
export default function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [keywordInput, setKeywordInput] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [hasSearched, setHasSearched] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  // Map: productId -> { cartItemId, quantity }. Absent key = not in cart.
  const [cartQuantities, setCartQuantities] = useState({});
  const [busyProductId, setBusyProductId] = useState(null);
  const { user, isAuthenticated } = useAuth();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    loadProducts();
    loadCategories();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    loadCartQuantities();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated, user]);

  async function loadProducts(filters = {}) {
    setLoading(true);
    setError("");
    try {
      const list = await browseProducts(filters);
      setProducts(list ?? []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadCategories() {
    try {
      const list = await listCategories();
      setCategories(list ?? []);
    } catch {
      // Non-critical: if the category list fails to load, the keyword
      // search still works fine without a category dropdown.
      setCategories([]);
    }
  }

  async function loadCartQuantities() {
    if (!isAuthenticated || user?.role !== "CUSTOMER") {
      setCartQuantities({});
      return;
    }
    try {
      const items = await viewCart();
      const map = {};
      (items ?? []).forEach((item) => {
        map[item.productId] = { cartItemId: item.cartItemId, quantity: item.quantity };
      });
      setCartQuantities(map);
    } catch {
      setCartQuantities({});
    }
  }

  function handleSearchSubmit(e) {
    e.preventDefault();
    runSearch(keywordInput, categoryId);
  }

  function handleCategoryChange(e) {
    const value = e.target.value;
    setCategoryId(value);
    runSearch(keywordInput, value);
  }

  function runSearch(keyword, catId) {
    setHasSearched(Boolean(keyword) || Boolean(catId));
    loadProducts({ keyword, categoryId: catId || undefined });
  }

  async function handleIncrease(product) {
    setError("");
    const existing = cartQuantities[product.productId];
    const currentQty = existing ? existing.quantity : 0;
    const stock = product.stockQuantity ?? 0;

    if (currentQty + 1 > stock) {
      setError(
        `Không thể thêm quá số lượng hiện có trong kho — "${product.productName}" chỉ còn lại ${stock} sản phẩm.`
      );
      return;
    }

    setBusyProductId(product.productId);
    try {
      const updated = await addToCart({ productId: product.productId, quantity: 1 });
      setCartQuantities((prev) => ({
        ...prev,
        [product.productId]: { cartItemId: updated.cartItemId, quantity: updated.quantity },
      }));
      await refreshCartCount();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyProductId(null);
    }
  }

  async function handleDecrease(product) {
    setError("");
    const existing = cartQuantities[product.productId];
    if (!existing) {
      return;
    }

    setBusyProductId(product.productId);
    try {
      const newQuantity = existing.quantity - 1;
      if (newQuantity <= 0) {
        await removeCartItem(existing.cartItemId);
        setCartQuantities((prev) => {
          const next = { ...prev };
          delete next[product.productId];
          return next;
        });
      } else {
        await updateCartItemQuantity(existing.cartItemId, newQuantity);
        setCartQuantities((prev) => ({
          ...prev,
          [product.productId]: { ...existing, quantity: newQuantity },
        }));
      }
      await refreshCartCount();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyProductId(null);
    }
  }

  return (
    <Container className="py-4">
      <h3 className="text-success fw-bold mb-4">Sản phẩm tươi ngon mỗi ngày</h3>

      <Form onSubmit={handleSearchSubmit} className="mb-4">
        <Row className="g-2 align-items-end">
          <Col xs={12} md={6}>
            <Form.Label className="small text-muted mb-1">Tìm kiếm sản phẩm</Form.Label>
            <InputGroup>
              <Form.Control
                type="search"
                placeholder="Nhập tên sản phẩm, ví dụ: Táo, Xoài..."
                value={keywordInput}
                onChange={(e) => setKeywordInput(e.target.value)}
              />
              <Button type="submit" variant="success">
                Tìm kiếm
              </Button>
            </InputGroup>
          </Col>
          <Col xs={12} md={4}>
            <Form.Label className="small text-muted mb-1">Danh mục</Form.Label>
            <Form.Select value={categoryId} onChange={handleCategoryChange}>
              <option value="">Tất cả danh mục</option>
              {categories.map((cat) => (
                <option key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryName}
                </option>
              ))}
            </Form.Select>
          </Col>
        </Row>
      </Form>

      {loading ? (
        <Container className="d-flex justify-content-center py-5">
          <Spinner animation="border" />
        </Container>
      ) : (
        <>
          {error && <Alert variant="danger">{error}</Alert>}

          {products.length === 0 ? (
            <Alert variant="light" className="border text-center">
              {hasSearched ? (
                <>
                  Không tìm thấy sản phẩm phù hợp với tiêu chí của bạn.
                  <div className="text-muted small mt-1">
                    Hãy thử một từ khóa khác hoặc chọn "Tất cả danh mục".
                  </div>
                </>
              ) : (
                "Hiện chưa có sản phẩm nào đang bán."
              )}
            </Alert>
          ) : (
            <Row xs={1} sm={2} md={3} lg={4} className="g-4">
              {products.map((product) => {
                const inCart = cartQuantities[product.productId];
                const isBusy = busyProductId === product.productId;

                return (
                  <Col key={product.productId}>
                    <Card className="h-100 shadow-sm border-0">
                      <Link
                        to={`/products/${product.productId}`}
                        className="text-decoration-none text-dark"
                      >
                        <Card.Img
                          variant="top"
                          src={product.imageUrl || "https://placehold.co/300x200?text=FreshMart"}
                          style={{ height: 180, objectFit: "cover" }}
                        />
                        <Card.Body>
                          <Card.Title className="fs-6">{product.productName}</Card.Title>
                          <Card.Text className="text-success fw-bold mb-0">
                            {Number(product.price ?? 0).toLocaleString("vi-VN")} đ
                          </Card.Text>
                        </Card.Body>
                      </Link>
                      <Card.Footer className="bg-white border-0 pt-0">
                        {isAuthenticated && user?.role === "CUSTOMER" ? (
                          inCart ? (
                            <div className="d-flex align-items-center gap-2">
                              <Button
                                size="sm"
                                variant="outline-secondary"
                                disabled={isBusy}
                                onClick={() => handleDecrease(product)}
                              >
                                −
                              </Button>
                              <span className="fw-semibold flex-grow-1 text-center">
                                {inCart.quantity}
                              </span>
                              <Button
                                size="sm"
                                variant="outline-success"
                                disabled={isBusy}
                                onClick={() => handleIncrease(product)}
                              >
                                {isBusy ? <Spinner animation="border" size="sm" /> : "+"}
                              </Button>
                            </div>
                          ) : (
                            <Button
                              size="sm"
                              variant="success"
                              className="w-100"
                              disabled={isBusy}
                              onClick={() => handleIncrease(product)}
                            >
                              {isBusy ? <Spinner animation="border" size="sm" /> : "Thêm vào giỏ"}
                            </Button>
                          )
                        ) : (
                          <Badge bg="light" text="dark" className="w-100 py-2 border fw-normal">
                            Đăng nhập để mua hàng
                          </Badge>
                        )}
                      </Card.Footer>
                    </Card>
                  </Col>
                );
              })}
            </Row>
          )}
        </>
      )}
    </Container>
  );
}
