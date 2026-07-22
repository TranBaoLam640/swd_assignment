import { Link, useNavigate } from "react-router-dom";
import { Navbar, Container, Nav, Badge } from "react-bootstrap";
import { useAuth, useCart } from "../../app/context";

export default function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const { cartCount } = useCart();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <Navbar expand="lg" className="fm-navbar sticky-top">
      <Container>
        <Navbar.Brand as={Link} to="/">
          <span className="fm-brand-mark">F</span>
          <span>FreshMart</span>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="main-navbar" />
        <Navbar.Collapse id="main-navbar">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">
              San pham
            </Nav.Link>
            {user?.role === "MANAGER" && (
              <>
                <Nav.Link as={Link} to="/manager/products">
                  Quan ly san pham
                </Nav.Link>
                <Nav.Link as={Link} to="/manager/shops">
                  Quan ly shop
                </Nav.Link>
              </>
            )}
          </Nav>
          <Nav className="align-items-lg-center gap-lg-2">
            {isAuthenticated && user?.role === "CUSTOMER" && (
              <>
                <Nav.Link as={Link} to="/orders">
                  Don hang
                </Nav.Link>
                <Nav.Link as={Link} to="/addresses">
                  Dia chi
                </Nav.Link>
                <Nav.Link as={Link} to="/cart">
                  Gio hang <Badge bg="light" text="dark">{cartCount}</Badge>
                </Nav.Link>
              </>
            )}
            {isAuthenticated ? (
              <>
                <span className="fm-user-pill small">Xin chao, {user?.fullName}</span>
                <Nav.Link as="span" role="button" onClick={handleLogout}>
                  Dang xuat
                </Nav.Link>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login">
                  Dang nhap
                </Nav.Link>
                <Nav.Link as={Link} to="/register">
                  Dang ky
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
