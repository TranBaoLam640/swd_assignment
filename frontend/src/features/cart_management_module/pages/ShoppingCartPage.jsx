import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Table, Button, Alert, Spinner, Form, Badge } from "react-bootstrap";
import { viewCart, updateCartItemQuantity, removeCartItem } from "../services/cartService";
import { getProduct } from "../../product_management_module/services/productService";
import { useCart } from "../../../app/context";
import { calculateLineTotal, formatPackage, formatUnitPrice, getPackageCount } from "../../../common/utils/measure";

/**
 * Shopping Cart page — UC12 (View Shopping Cart) + UC13 (Update Cart Item
 * Quantity) + UC14 (Remove Item from Cart). CartItemResponse only carries
 * productId/quantity, so each item's name/price/image/isActive is fetched
 * from the Product module and merged in on load. "Tiến hành thanh toán"
 * now that the Order module (UC33 - Checkout) is built.
 *
 * A product can go inactive (manager hid/discontinued it) after it was
 * already added to someone's cart — checkout's own BR-14/BR-20 revalidation
 * would reject it at that point anyway, so the row is dimmed here to warn
 * the customer up front rather than let them discover it only at checkout.
 */
export default function ShoppingCartPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const { refreshCartCount } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  async function loadCart() {
    setLoading(true);
    setError("");
    try {
      const cartItems = await viewCart();
      const withProducts = await Promise.all(
        (cartItems ?? []).map(async (item) => {
          try {
            const product = await getProduct(item.productId);
            return { ...item, product };
          } catch {
            return { ...item, product: null };
          }
        })
      );
      setItems(withProducts);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleQuantityChange(item, packageCount) {
    setError("");
    try {
      const quantity = Math.max(0, Math.floor(Number(packageCount)));
      const cartItemId = item.cartItemId;
      const updated = await updateCartItemQuantity(cartItemId, quantity);
      if (updated === null) {
        // Backend forwarded to remove (quantity reached 0) — UC13 alt flow.
        setItems((prev) => prev.filter((item) => item.cartItemId !== cartItemId));
      } else {
        setItems((prev) =>
          prev.map((item) =>
            item.cartItemId === cartItemId ? { ...item, quantity: updated.quantity } : item
          )
        );
      }
      await refreshCartCount();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleRemove(cartItemId) {
    setError("");
    try {
      await removeCartItem(cartItemId);
      setItems((prev) => prev.filter((item) => item.cartItemId !== cartItemId));
      await refreshCartCount();
    } catch (err) {
      setError(err.message);
    }
  }

  const total = items.reduce(
    (sum, item) => sum + calculateLineTotal(item.product, item.quantity),
    0
  );
  const hasInactiveItem = items.some((item) => item.product && item.product.isActive === false);

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <h3 className="text-success fw-bold mb-4">Giỏ hàng của bạn</h3>

      {error && <Alert variant="danger">{error}</Alert>}
      {hasInactiveItem && (
        <Alert variant="warning">
          Một hoặc nhiều sản phẩm trong giỏ hàng của bạn hiện đã ngừng bán (làm mờ bên dưới) — bạn
          nên xóa khỏi giỏ trước khi thanh toán, vì các sản phẩm này sẽ bị từ chối ở bước xác nhận
          đơn hàng.
        </Alert>
      )}

      {items.length === 0 ? (
        <Alert variant="light" className="border text-center">
          Giỏ hàng trống. <Link to="/">Quay lại mua sắm</Link>
        </Alert>
      ) : (
        <>
          <Table responsive hover align="middle">
            <thead>
              <tr>
                <th>Sản phẩm</th>
                <th style={{ width: 140 }}>Đơn giá</th>
                <th style={{ width: 140 }}>Số lượng</th>
                <th style={{ width: 140 }}>Thành tiền</th>
                <th style={{ width: 100 }}></th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => {
                const isInactive = item.product && item.product.isActive === false;

                return (
                  <tr
                    key={item.cartItemId}
                    className={isInactive ? "opacity-50" : undefined}
                    style={isInactive ? { opacity: 0.5 } : undefined}
                  >
                    <td>
                      {item.product ? (
                        <Link
                          to={`/products/${item.productId}`}
                          className="text-decoration-none"
                        >
                          {item.product.productName}
                        </Link>
                      ) : (
                        `Sản phẩm #${item.productId}`
                      )}
                      {isInactive && (
                        <Badge bg="secondary" className="ms-2">
                          Ngừng bán
                        </Badge>
                      )}
                    </td>
                    <td>{formatUnitPrice(item.product)}</td>
                    <td>
                      <Form.Control
                        type="number"
                        min={0}
                        step={1}
                        value={getPackageCount(item.product, item.quantity)}
                        onChange={(e) =>
                          handleQuantityChange(item, e.target.value)
                        }
                      />
                      <span className="text-muted small">x {formatPackage(item.product)}</span>
                    </td>
                    <td>
                      {Number(calculateLineTotal(item.product, item.quantity)).toLocaleString("vi-VN")} đ
                    </td>
                    <td>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleRemove(item.cartItemId)}
                      >
                        Xóa
                      </Button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>

          <div className="d-flex justify-content-between align-items-center">
            <h5 className="mb-0">
              Tổng cộng:{" "}
              <span className="text-success fw-bold">{total.toLocaleString("vi-VN")} đ</span>
            </h5>
            <Button variant="success" onClick={() => navigate("/checkout")}>
              Tiến hành thanh toán
            </Button>
          </div>
        </>
      )}
    </Container>
  );
}
