import { useState } from "react";
import { Link } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { register } from "../services/authService";

const ROLE_OPTIONS = [
  { value: "CUSTOMER", label: "Khách hàng — mua sắm trên FreshMart" },
  { value: "MANAGER", label: "Quản lý — tạo và quản lý sản phẩm/tồn kho" },
  { value: "SHIPPER", label: "Người giao hàng" },
];

/**
 * Register page — controlled form, calls authService.register() against
 * POST /api/v1/auth/register.
 *
 * Includes a role selector (CUSTOMER/MANAGER/SHIPPER) so demo/test accounts
 * for any of these roles can be created without a DB admin. ADMIN is
 * intentionally not offered here — the backend rejects it even if sent.
 *
 * Also includes a "Xác nhận mật khẩu" field (UC02 - Sign Up). The match is
 * checked client-side for instant feedback AND server-side in
 * AuthServiceImpl.register() (PasswordMismatchException) — the client check
 * is just a UX shortcut, not a substitute for the real validation.
 */
export default function RegisterPage() {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    password: "",
    confirmPassword: "",
    role: "CUSTOMER",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess(false);

    if (formData.password !== formData.confirmPassword) {
      setError("Mật khẩu xác nhận không khớp với mật khẩu đã nhập.");
      return;
    }

    setLoading(true);
    try {
      await register(formData);
      setSuccess(true);
    } catch (err) {
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        setError(err.fieldErrors.map((fe) => fe.message).join(" "));
      } else {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <Container
      fluid
      className="d-flex align-items-center justify-content-center bg-light"
      style={{ minHeight: "100vh" }}
    >
      <Card className="shadow-sm border-0 p-4" style={{ width: "100%", maxWidth: 420 }}>
        <Card.Body>
          <h3 className="text-center text-success fw-bold mb-1">FreshMart</h3>
          <p className="text-center text-muted mb-4">Tạo tài khoản mới</p>

          {error && <Alert variant="danger">{error}</Alert>}
          {success && (
            <Alert variant="success">
              Đăng ký thành công! Vui lòng <Link to="/login">đăng nhập</Link>.
            </Alert>
          )}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="registerFullName">
              <Form.Label>Họ và tên</Form.Label>
              <Form.Control
                type="text"
                name="fullName"
                placeholder="Nguyễn Văn A"
                value={formData.fullName}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="registerEmail">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                placeholder="you@example.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="registerPhoneNumber">
              <Form.Label>Số điện thoại</Form.Label>
              <Form.Control
                type="tel"
                name="phoneNumber"
                placeholder="0912345678"
                value={formData.phoneNumber}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="registerPassword">
              <Form.Label>Mật khẩu</Form.Label>
              <Form.Control
                type="password"
                name="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="registerConfirmPassword">
              <Form.Label>Xác nhận mật khẩu</Form.Label>
              <Form.Control
                type="password"
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-4" controlId="registerRole">
              <Form.Label>Vai trò</Form.Label>
              <Form.Select name="role" value={formData.role} onChange={handleChange} required>
                {ROLE_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Button type="submit" variant="success" className="w-100" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : "Đăng ký"}
            </Button>
          </Form>

          <p className="text-center text-muted mt-4 mb-0">
            Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
          </p>
        </Card.Body>
      </Card>
    </Container>
  );
}
