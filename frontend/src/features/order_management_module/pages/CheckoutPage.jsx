import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Container, Table, Button, Alert, Spinner, Form, Card } from "react-bootstrap";
import { viewCart } from "../../cart_management_module/services/cartService";
import { getProduct } from "../../product_management_module/services/productService";
import { checkout, createPaymentUrl } from "../services/orderService";
import { listAddresses } from "../../address_management_module/services/addressService";
import AddressSelector from "../../address_management_module/components/AddressSelector";
import { useCart } from "../../../app/context";

/**
 * Checkout page — UC33 (Checkout and Place Order).
 *
 * A shipping address is now mandatory before placing an order (Manage
 * Shipping Address requirement): if the customer has zero saved addresses,
 * AddressSelector renders a blocking "add address" form instead of a radio
 * list, and "Đặt hàng" stays disabled until an address exists and is
 * selected. Once at least one address exists, the customer picks one
 * (default pre-selected) or adds another — picking a new default there
 * unsets every other address, mirrored server-side by AddressServiceImpl.
 *
 * For ONLINE payment, after the order is created (status PENDING_PAYMENT)
 * this immediately requests a VNPAY payment URL and redirects the whole
 * browser there (window.location, not react-router — VNPAY is a separate
 * site). For COD, the order is already CONFIRMED, so it goes straight to
 * the order detail page.
 */
export default function CheckoutPage() {
  const [items, setItems] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    loadCart();
    loadAddresses();
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

  async function loadAddresses(preselectId = null) {
    try {
      const list = await listAddresses();
      setAddresses(list ?? []);
      if (preselectId) {
        setSelectedAddressId(preselectId);
      } else {
        const def = (list ?? []).find((a) => a.defaultAddress);
        setSelectedAddressId(def ? def.addressId : list?.[0]?.addressId ?? null);
      }
    } catch (err) {
      setError(err.message);
    }
  }

  async function handlePlaceOrder() {
    if (!selectedAddressId) {
      setError("Vui lòng chọn hoặc thêm địa chỉ giao hàng trước khi đặt hàng.");
      return;
    }

    setError("");
    setPlacing(true);
    try {
      const order = await checkout({ paymentMethod, addressId: selectedAddressId });
      await refreshCartCount();

      if (paymentMethod === "ONLINE") {
        const { paymentUrl } = await createPaymentUrl(order.orderId);
        window.location.href = paymentUrl;
        return;
      }

      navigate(`/orders/${order.orderId}`);
    } catch (err) {
      setError(err.message);
      setPlacing(false);
    }
  }

  const total = items.reduce(
    (sum, item) => sum + (item.product?.price ?? 0) * item.quantity,
    0
  );

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (items.length === 0) {
    return (
      <Container className="py-4">
        <Alert variant="light" className="border text-center">
          Giỏ hàng trống, không có gì để thanh toán. <Link to="/">Quay lại mua sắm</Link>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-4" style={{ maxWidth: 720 }}>
      <h3 className="text-success fw-bold mb-4">Xác nhận đơn hàng</h3>

      {error && <Alert variant="danger">{error}</Alert>}

      <Table responsive hover align="middle">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th style={{ width: 100 }}>SL</th>
            <th style={{ width: 140 }}>Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.cartItemId}>
              <td>{item.product?.productName ?? `Sản phẩm #${item.productId}`}</td>
              <td>{item.quantity}</td>
              <td>{Number((item.product?.price ?? 0) * item.quantity).toLocaleString("vi-VN")} đ</td>
            </tr>
          ))}
        </tbody>
      </Table>

      <div className="d-flex justify-content-end mb-4">
        <h5>
          Tổng cộng: <span className="text-success fw-bold">{total.toLocaleString("vi-VN")} đ</span>
        </h5>
      </div>

      <AddressSelector
        addresses={addresses}
        selectedAddressId={selectedAddressId}
        onSelect={setSelectedAddressId}
        onAddressesChanged={loadAddresses}
      />

      <Card className="border-0 shadow-sm p-3 mb-4">
        <Card.Body>
          <Card.Title className="fs-6">Phương thức thanh toán</Card.Title>
          <Form>
            <Form.Check
              type="radio"
              name="paymentMethod"
              id="paymentCOD"
              label="Thanh toán khi nhận hàng (COD)"
              checked={paymentMethod === "COD"}
              onChange={() => setPaymentMethod("COD")}
              className="mb-2"
            />
            <Form.Check
              type="radio"
              name="paymentMethod"
              id="paymentOnline"
              label="Thanh toán online qua VNPAY"
              checked={paymentMethod === "ONLINE"}
              onChange={() => setPaymentMethod("ONLINE")}
            />
          </Form>
        </Card.Body>
      </Card>

      <Button
        variant="success"
        className="w-100"
        disabled={placing || !selectedAddressId}
        onClick={handlePlaceOrder}
      >
        {placing ? <Spinner animation="border" size="sm" /> : "Đặt hàng"}
      </Button>
    </Container>
  );
}
