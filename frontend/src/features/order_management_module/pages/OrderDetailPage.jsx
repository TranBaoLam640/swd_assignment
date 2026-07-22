import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Table, Badge, Alert, Spinner, Button, Modal, Form } from "react-bootstrap";
import { getOrder, cancelOrder, createPaymentUrl } from "../services/orderService";
import { createReview, listMyOrderReviews } from "../../product_management_module/services/reviewService";

const STATUS_LABELS = {
  CREATED: { label: "Đang xử lý", variant: "secondary" },
  FRUIT_HELD: { label: "Đang giữ hàng", variant: "info" },
  PENDING_PAYMENT: { label: "Chờ thanh toán", variant: "warning" },
  CONFIRMED: { label: "Đã xác nhận", variant: "success" },
  CANCELLED: { label: "Đã hủy", variant: "danger" },
  EXPIRED: { label: "Đã hết hạn", variant: "dark" },
};

const CANCELLABLE_STATUSES = ["FRUIT_HELD", "PENDING_PAYMENT", "CONFIRMED"];

function isReviewableOrder(order) {
  if (order.status !== "CONFIRMED") return false;
  if (order.paymentMethod === "COD") return true;
  return order.paymentMethod === "ONLINE" && order.paymentStatus === "SUCCESS";
}

export default function OrderDetailPage() {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showCancelModal, setShowCancelModal] = useState(false);
  const [cancelReason, setCancelReason] = useState("");
  const [cancelling, setCancelling] = useState(false);
  const [payingAgain, setPayingAgain] = useState(false);
  const [reviewItem, setReviewItem] = useState(null);
  const [reviewForm, setReviewForm] = useState({
    rating: 5,
    comment: "",
    imageUrl: "",
    videoUrl: "",
  });
  const [submittingReview, setSubmittingReview] = useState(false);

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
      const reviewList = await listMyOrderReviews(orderId).catch(() => []);
      setReviews(reviewList ?? []);
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

  function openReviewModal(item) {
    setReviewItem(item);
    setReviewForm({ rating: 5, comment: "", imageUrl: "", videoUrl: "" });
  }

  function handleReviewChange(e) {
    const { name, value } = e.target;
    setReviewForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmitReview() {
    setSubmittingReview(true);
    setError("");
    try {
      const created = await createReview({
        orderId: order.orderId,
        productId: reviewItem.productId,
        rating: Number(reviewForm.rating),
        comment: reviewForm.comment,
        imageUrl: reviewForm.imageUrl,
        videoUrl: reviewForm.videoUrl,
      });
      setReviews((prev) => [...prev, created]);
      setReviewItem(null);
    } catch (err) {
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        setError(err.fieldErrors.map((fe) => fe.message).join(" "));
      } else {
        setError(err.message);
      }
    } finally {
      setSubmittingReview(false);
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
          Quay lại danh sách đơn hàng
        </Button>
      </Container>
    );
  }

  const statusInfo = STATUS_LABELS[order.status] ?? { label: order.status, variant: "secondary" };
  const canCancel = CANCELLABLE_STATUSES.includes(order.status);
  const canPayAgain = order.status === "PENDING_PAYMENT" && order.paymentMethod === "ONLINE";
  const canReview = isReviewableOrder(order);
  const reviewedProductIds = new Set(reviews.map((review) => review.productId));

  return (
    <Container className="py-4" style={{ maxWidth: 860 }}>
      <Button as={Link} to="/orders" variant="outline-secondary" size="sm" className="mb-3">
        Quay lại danh sách đơn hàng
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
          <span>Đơn hàng đang chờ thanh toán. Bạn có thể tạo lại link VNPAY để thanh toán tiếp.</span>
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
            <th style={{ width: 80 }}>SL</th>
            <th style={{ width: 130 }}>Đơn giá</th>
            <th style={{ width: 130 }}>Thành tiền</th>
            <th style={{ width: 130 }}>Đánh giá</th>
          </tr>
        </thead>
        <tbody>
          {order.items.map((item) => {
            const reviewed = reviewedProductIds.has(item.productId);
            return (
              <tr key={item.orderItemId}>
                <td>{item.productName ?? `Sản phẩm #${item.productId}`}</td>
                <td>{item.quantity}</td>
                <td>{Number(item.priceAtPurchase).toLocaleString("vi-VN")} đ</td>
                <td>{Number(item.subtotal).toLocaleString("vi-VN")} đ</td>
                <td>
                  {reviewed ? (
                    <Badge bg="success">Đã đánh giá</Badge>
                  ) : canReview ? (
                    <Button variant="outline-success" size="sm" onClick={() => openReviewModal(item)}>
                      Đánh giá
                    </Button>
                  ) : (
                    <span className="text-muted">-</span>
                  )}
                </td>
              </tr>
            );
          })}
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

      <Modal show={Boolean(reviewItem)} onHide={() => setReviewItem(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Đánh giá sản phẩm</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {reviewItem && (
            <>
              <p className="fw-semibold mb-3">{reviewItem.productName ?? `Sản phẩm #${reviewItem.productId}`}</p>
              <Form.Group className="mb-3" controlId="reviewRating">
                <Form.Label>Rating</Form.Label>
                <Form.Select name="rating" value={reviewForm.rating} onChange={handleReviewChange} required>
                  <option value={5}>5 - Rất hài lòng</option>
                  <option value={4}>4 - Hài lòng</option>
                  <option value={3}>3 - Bình thường</option>
                  <option value={2}>2 - Chưa hài lòng</option>
                  <option value={1}>1 - Không hài lòng</option>
                </Form.Select>
              </Form.Group>
              <Form.Group className="mb-3" controlId="reviewComment">
                <Form.Label>Comment</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="comment"
                  value={reviewForm.comment}
                  onChange={handleReviewChange}
                />
              </Form.Group>
              <Form.Group className="mb-3" controlId="reviewImageUrl">
                <Form.Label>Image URL</Form.Label>
                <Form.Control name="imageUrl" value={reviewForm.imageUrl} onChange={handleReviewChange} />
              </Form.Group>
              <Form.Group controlId="reviewVideoUrl">
                <Form.Label>Video URL</Form.Label>
                <Form.Control name="videoUrl" value={reviewForm.videoUrl} onChange={handleReviewChange} />
              </Form.Group>
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setReviewItem(null)}>
            Đóng
          </Button>
          <Button variant="success" disabled={submittingReview} onClick={handleSubmitReview}>
            {submittingReview ? <Spinner animation="border" size="sm" /> : "Gửi đánh giá"}
          </Button>
        </Modal.Footer>
      </Modal>

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
          <Button variant="danger" disabled={!cancelReason.trim() || cancelling} onClick={handleConfirmCancel}>
            {cancelling ? <Spinner animation="border" size="sm" /> : "Xác nhận hủy"}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}
