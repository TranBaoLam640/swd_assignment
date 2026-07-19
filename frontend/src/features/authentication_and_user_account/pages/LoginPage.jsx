import { useState } from "react";
import { Link } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { login } from "../services/authService";

/**
 * Login page — controlled form, calls authService.login() against
 * POST /api/v1/auth/login.
 */
export default function LoginPage() {
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);
    try {
      const { accessToken, userInfo } = await login(formData);
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("userInfo", JSON.stringify(userInfo));
      setSuccess(`Đăng nhập thành công, xin chào ${userInfo.fullName}!`);
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
      <Card className="shadow-sm border-0 p-4" style={{ width: "100%", maxWidth: 400 }}>
        <Card.Body>
          <h3 className="text-center text-success fw-bold mb-1">FreshMart</h3>
          <p className="text-center text-muted mb-4">Đăng nhập vào tài khoản của bạn</p>

          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="loginEmail">
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

            <Form.Group className="mb-4" controlId="loginPassword">
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

            <Button type="submit" variant="success" className="w-100" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : "Đăng nhập"}
            </Button>
          </Form>

          <p className="text-center text-muted mt-4 mb-0">
            Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
          </p>
        </Card.Body>
      </Card>
    </Container>
  );
}
