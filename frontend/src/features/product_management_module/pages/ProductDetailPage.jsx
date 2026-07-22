import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Row, Col, Image, Badge, Button, Form, Alert, Spinner, Card } from "react-bootstrap";
import RatingStars from "../components/RatingStars";
import { getProduct } from "../services/productService";
import { listProductReviews } from "../services/reviewService";
import { addToCart, viewCart } from "../../cart_management_module/services/cartService";
import { useAuth, useCart } from "../../../app/context";

export default function ProductDetailPage() {
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [quantity, setQuantity] = useState(1);
  const [cartQuantity, setCartQuantity] = useState(0);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const { user, isAuthenticated } = useAuth();
  const { refreshCartCount } = useCart();

  useEffect(() => {
    loadProduct();
    loadCartQuantity();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId, isAuthenticated, user]);

  async function loadProduct() {
    setLoading(true);
    setError("");
    try {
      const [productData, reviewList] = await Promise.all([
        getProduct(productId),
        listProductReviews(productId).catch(() => []),
      ]);
      setProduct(productData);
      setReviews(reviewList ?? []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadCartQuantity() {
    if (!isAuthenticated || user?.role !== "CUSTOMER") {
      setCartQuantity(0);
      return;
    }
    try {
      const items = await viewCart();
      const existing = (items ?? []).find((item) => item.productId === Number(productId));
      setCartQuantity(existing ? existing.quantity : 0);
    } catch {
      setCartQuantity(0);
    }
  }

  async function handleAddToCart() {
    setError("");
    setMessage("");

    const stock = product?.stockQuantity ?? 0;
    const remaining = stock - cartQuantity;
    if (quantity > remaining) {
      setError(
        remaining > 0
          ? `Không thể thêm quá số lượng còn trong kho. Bạn chỉ có thể thêm tối đa ${remaining} sản phẩm nữa.`
          : `Không thể thêm vì số lượng trong giỏ hàng đã bằng số lượng tồn kho (${stock}).`
      );
      return;
    }

    setAdding(true);
    try {
      await addToCart({ productId: Number(productId), quantity });
      await refreshCartCount();
      setCartQuantity((prev) => prev + quantity);
      setMessage("Đã thêm vào giỏ hàng.");
    } catch (err) {
      setError(err.message);
    } finally {
      setAdding(false);
    }
  }

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error && !product) {
    return (
      <Container className="py-4">
        <Alert variant="danger">{error}</Alert>
        <Link to="/">Quay lại danh sách sản phẩm</Link>
      </Container>
    );
  }

  const stock = product.stockQuantity ?? 0;
  const remaining = Math.max(0, stock - cartQuantity);

  return (
    <Container className="py-4">
      <Link to="/" className="btn btn-outline-secondary btn-sm mb-3">
        Quay lại danh sách sản phẩm
      </Link>

      {error && <Alert variant="danger">{error}</Alert>}
      {message && <Alert variant="success">{message}</Alert>}

      <Row className="g-4">
        <Col md={5}>
          <Image
            src={product.imageUrl || "https://placehold.co/500x400?text=FreshMart"}
            fluid
            rounded
          />
        </Col>
        <Col md={7}>
          <h3 className="fw-bold">{product.productName}</h3>
          <div className="mb-2">
            <RatingStars value={product.averageRating ?? 0} count={product.reviewCount ?? 0} />
          </div>
          <Badge bg={product.isActive ? "success" : "secondary"} className="mb-3">
            {product.isActive ? "Đang bán" : "Ngừng bán"}
          </Badge>
          <p className="text-muted">{product.description || "Chưa có mô tả."}</p>
          <h4 className="text-success fw-bold">
            {Number(product.price ?? 0).toLocaleString("vi-VN")} đ
          </h4>
          <p className="text-muted small mb-3">
            Còn lại trong kho: {stock} sản phẩm
            {cartQuantity > 0 && ` (giỏ hàng của bạn đã có ${cartQuantity})`}
          </p>

          {isAuthenticated && user?.role === "CUSTOMER" ? (
            <div className="d-flex align-items-center gap-2 mt-3" style={{ maxWidth: 320 }}>
              <Form.Control
                type="number"
                min={1}
                max={Math.max(1, remaining)}
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
              />
              <Button
                variant="success"
                disabled={adding || remaining <= 0}
                onClick={handleAddToCart}
                className="text-nowrap"
              >
                {adding ? <Spinner animation="border" size="sm" /> : "Thêm vào giỏ"}
              </Button>
            </div>
          ) : (
            <Alert variant="light" className="border mt-3">
              <Link to="/login">Đăng nhập</Link> để thêm sản phẩm vào giỏ hàng.
            </Alert>
          )}
        </Col>
      </Row>

      <section className="mt-5">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h4 className="fw-bold mb-0">Đánh giá sản phẩm</h4>
          <RatingStars value={product.averageRating ?? 0} count={product.reviewCount ?? 0} />
        </div>

        {reviews.length === 0 ? (
          <Alert variant="light" className="border">
            Chưa có đánh giá nào cho sản phẩm này.
          </Alert>
        ) : (
          <div className="d-grid gap-3">
            {reviews.map((review) => (
              <Card key={review.reviewId} className="border-0 shadow-sm">
                <Card.Body>
                  <div className="d-flex justify-content-between gap-3 mb-2">
                    <div>
                      <div className="fw-semibold">{review.reviewerName || "Khách hàng"}</div>
                      <RatingStars value={review.rating} size="sm" />
                    </div>
                    <span className="text-muted small">
                      {review.createdAt ? new Date(review.createdAt).toLocaleString("vi-VN") : ""}
                    </span>
                  </div>

                  {review.comment && <p className="mb-2">{review.comment}</p>}
                  {review.imageUrl && (
                    <div className="mb-2">
                      <a href={review.imageUrl} target="_blank" rel="noreferrer">
                        Image URL
                      </a>
                    </div>
                  )}
                  {review.videoUrl && (
                    <div>
                      <a href={review.videoUrl} target="_blank" rel="noreferrer">
                        Video URL
                      </a>
                    </div>
                  )}
                </Card.Body>
              </Card>
            ))}
          </div>
        )}
      </section>
    </Container>
  );
}
