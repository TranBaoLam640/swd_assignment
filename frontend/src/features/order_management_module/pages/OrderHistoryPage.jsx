import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Container, Table, Badge, Alert, Spinner } from "react-bootstrap";
import { listMyOrders } from "../services/orderService";

const STATUS_LABELS = {
  CREATED: { label: "Đang xử lý", variant: "secondary" },
  FRUIT_HELD: { label: "Đang giữ hàng", variant: "info" },
  PENDING_PAYMENT: { label: "Chờ thanh toán", variant: "warning" },
  CONFIRMED: { label: "Đã xác nhận", variant: "success" },
  CANCELLED: { label: "Đã hủy", variant: "danger" },
  EXPIRED: { label: "Đã hết hạn", variant: "dark" },
};

const PAYMENT_METHOD_LABELS = {
  COD: "COD",
  ONLINE: "Online",
};

/** Order History page - UC15. */
export default function OrderHistoryPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadOrders();
  }, []);

  async function loadOrders() {
    setLoading(true);
    setError("");
    try {
      const list = await listMyOrders();
      setOrders(list ?? []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <h3 className="text-success fw-bold mb-4">Đơn hàng của tôi</h3>

      {error && <Alert variant="danger">{error}</Alert>}

      {orders.length === 0 ? (
        <Alert variant="light" className="border text-center">
          Bạn chưa có đơn hàng nào. <Link to="/">Tiếp tục mua sắm</Link>
        </Alert>
      ) : (
        <Table responsive hover align="middle">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Ngày đặt</th>
              <th>Phương thức</th>
              <th>Tổng tiền</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => {
              const statusInfo = STATUS_LABELS[order.status] ?? { label: order.status, variant: "secondary" };
              return (
                <tr key={order.orderId}>
                  <td>#{order.orderId}</td>
                  <td>{new Date(order.createdAt).toLocaleString("vi-VN")}</td>
                  <td>{PAYMENT_METHOD_LABELS[order.paymentMethod] ?? order.paymentMethod ?? "-"}</td>
                  <td>{Number(order.totalAmount ?? 0).toLocaleString("vi-VN")} đ</td>
                  <td>
                    <Badge bg={statusInfo.variant}>{statusInfo.label}</Badge>
                  </td>
                  <td>
                    <Link to={`/orders/${order.orderId}`}>Xem chi tiết</Link>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </Table>
      )}
    </Container>
  );
}
