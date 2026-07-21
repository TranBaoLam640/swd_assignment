import { Link, useNavigate } from "react-router-dom";
import { Navbar, Container, Nav, Badge } from "react-bootstrap";
import { useAuth, useCart } from "../../app/context";

/**
 * Global top navigation — brand, cart badge, and auth-aware links:
 * guest sees Đăng nhập/Đăng ký; a logged-in customer sees "Đơn hàng của
 * tôi" + "Địa chỉ của tôi" + the cart badge + logout; a manager also sees
 * the "Quản lý sản phẩm" link. Mirrors SecurityConfig's role-based path
 * rules so the nav never links somewhere the current role isn't allowed
 * to call.
 */
export default function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const { cartCount } = useCart();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <Navbar bg="success" variant="dark" expand="lg" className="mb-4">
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold">
          FreshMart
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="main-navbar" />
        <Navbar.Collapse id="main-navbar">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">
              Sản phẩm
            </Nav.Link>
            {user?.role === "MANAGER" && (
              <Nav.Link as={Link} to="/manager/products">
                Quản lý sản phẩm
              </Nav.Link>
            )}
          </Nav>
          <Nav className="align-items-lg-center gap-lg-3">
            {isAuthenticated && user?.role === "CUSTOMER" && (
              <>
                <Nav.Link as={Link} to="/orders">
                  Đơn hàng của tôi
                </Nav.Link>
                <Nav.Link as={Link} to="/addresses">
                  Địa chỉ của tôi
                </Nav.Link>
                <Nav.Link as={Link} to="/cart">
                  Giỏ hàng <Badge bg="light" text="dark">{cartCount}</Badge>
                </Nav.Link>
              </>
            )}
            {isAuthenticated ? (
              <>
                <span className="text-white small">Xin chào, {user?.fullName}</span>
                <Nav.Link as="span" role="button" className="text-white" onClick={handleLogout}>
                  Đăng xuất
                </Nav.Link>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login">
                  Đăng nhập
                </Nav.Link>
                <Nav.Link as={Link} to="/register">
                  Đăng ký
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
