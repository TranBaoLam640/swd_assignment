import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Table, Badge, Alert, Spinner, Button, Modal, Form } from "react-bootstrap";
import { getOrder, cancelOrder, createPaymentUrl } from "../services/orderService";

const STATUS_LABELS = {
  CREATED: { label: "Đang xử lý", variant: "secondary" },
  FRUIT_HELD: { label: "Đang giữ hàng", variant: "info" },
  PENDING_PAYMENT: { label: "Chờ thanh toán", variant: "warning" },
  CONFIRMED: { label: "Đã xác nhận", variant: "success" },
  CANCELLED: { label: "Đã hủy", variant: "danger" },
  EXPIRED: { label: "Đã hết hạn", variant: "dark" },
};

// Mirrors OrderStateMachine's ALLOWED_TRANSITIONS on the backend — only
// show "Hủy đơn" when the backend would actually accept a cancel.
const CANCELLABLE_STATUSES = ["FRUIT_HELD", "PENDING_PAYMENT", "CONFIRMED"];

/**
 * Order Detail page — UC16 (view) + UC17 (cancel).
 *
 * For an ONLINE order still PENDING_PAYMENT (e.g. the customer closed the
 * VNPAY tab without finishing, or the payment attempt failed some other
 * way before it expires), this also offers "Thanh toán lại": it just
 * re-requests a fresh payment-url the same way CheckoutPage does and
 * redirects there. If the order sits unpaid past the backend's timeout
 * (OrderExpiryScheduler, default 15 min), it flips to EXPIRED on its own
 * and this button disappears along with the other order actions.
 */
export default function OrderDetailPage() {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [cancelReason, setCancelReason] = useState("");
  const [cancelling, setCancelling] = useState(false);
  const [payingAgain, setPayingAgain] = useState(false);

  useEffect(() => {
    loadOrder();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [orderId]);

  async function loadOrder() {
    setLoading(true);
    setError("");
    try {
      const data = await getOrder(orderId);
      setOrder(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleConfirmCancel() {
    setCancelling(true);
    setError("");
    try {
      const updated = await cancelOrder(orderId, cancelReason);
      setOrder(updated);
      setShowCancelModal(false);
      setCancelReason("");
    } catch (err) {
      setError(err.message);
    } finally {
      setCancelling(false);
    }
  }

  async function handlePayAgain() {
    setError("");
    setPayingAgain(true);
    try {
      const { paymentUrl } = await createPaymentUrl(orderId);
      window.location.href = paymentUrl;
    } catch (err) {
      setError(err.message);
      setPayingAgain(false);
    }
  }

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error && !order) {
    return (
      <Container className="py-4">
        <Alert variant="danger">{error}</Alert>
        <Button as={Link} to="/orders" variant="outline-secondary" size="sm">
          ← Quay lại danh sách đơn hàng
        </Button>
      </Container>
    );
  }

  const statusInfo = STATUS_LABELS[order.status] ?? { label: order.status, variant: "secondary" };
  const canCancel = CANCELLABLE_STATUSES.includes(order.status);
  const canPayAgain = order.status === "PENDING_PAYMENT" && order.paymentMethod === "ONLINE";

  return (
    <Container className="py-4" style={{ maxWidth: 720 }}>
      <Button as={Link} to="/orders" variant="outline-secondary" size="sm" className="mb-3">
        ← Quay lại danh sách đơn hàng
      </Button>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="fw-bold mb-0">Đơn hàng #{order.orderId}</h3>
        <Badge bg={statusInfo.variant} className="fs-6">
          {statusInfo.label}
        </Badge>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      <p className="text-muted">
        Đặt lúc {new Date(order.createdAt).toLocaleString("vi-VN")} · Thanh toán:{" "}
        {order.paymentMethod === "COD" ? "Khi nhận hàng (COD)" : "Online qua VNPAY"}
      </p>

      {canPayAgain && (
        <Alert variant="warning" className="d-flex justify-content-between align-items-center">
          <span>
            Đơn hàng đang chờ thanh toán. Nếu bạn chưa hoàn tất thanh toán trên VNPAY, hãy thử lại bên dưới
            (đơn sẽ tự động bị hủy nếu quá thời gian chờ).
          </span>
          <Button variant="warning" size="sm" className="text-nowrap ms-3" disabled={payingAgain} onClick={handlePayAgain}>
            {payingAgain ? <Spinner animation="border" size="sm" /> : "Thanh toán lại"}
          </Button>
        </Alert>
      )}

      {order.cancelReason && (
        <Alert variant="light" className="border">
          Lý do hủy: {order.cancelReason}
        </Alert>
      )}

      <Table responsive hover align="middle">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th style={{ width: 100 }}>SL</th>
            <th style={{ width: 140 }}>Đơn giá</th>
            <th style={{ width: 140 }}>Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          {order.items.map((item) => (
            <tr key={item.orderItemId}>
              <td>{item.productName ?? `Sản phẩm #${item.productId}`}</td>
              <td>{item.quantity}</td>
              <td>{Number(item.priceAtPurchase).toLocaleString("vi-VN")} đ</td>
              <td>{Number(item.subtotal).toLocaleString("vi-VN")} đ</td>
            </tr>
          ))}
        </tbody>
      </Table>

      <div className="d-flex justify-content-end mb-4">
        <h5>
          Tổng cộng:{" "}
          <span className="text-success fw-bold">
            {Number(order.totalAmount).toLocaleString("vi-VN")} đ
          </span>
        </h5>
      </div>

      {canCancel && (
        <Button variant="outline-danger" onClick={() => setShowCancelModal(true)}>
          Hủy đơn hàng
        </Button>
      )}

      <Modal show={showCancelModal} onHide={() => setShowCancelModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Hủy đơn hàng #{order.orderId}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group controlId="cancelReason">
            <Form.Label>Lý do hủy</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              value={cancelReason}
              onChange={(e) => setCancelReason(e.target.value)}
              placeholder="Vui lòng cho biết lý do bạn muốn hủy đơn..."
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCancelModal(false)}>
            Đóng
          </Button>
          <Button
            variant="danger"
            disabled={!cancelReason.trim() || cancelling}
            onClick={handleConfirmCancel}
          >
            {cancelling ? <Spinner animation="border" size="sm" /> : "Xác nhận hủy"}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}
