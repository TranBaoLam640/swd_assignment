import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { useAuth } from "../../../app/context";
import { createShop, listShopsForManager } from "../services/shopService";

export default function ManagerShopListPage() {
  const { user } = useAuth();
  const [shops, setShops] = useState([]);
  const [formData, setFormData] = useState({
    shopName: "",
    shopAddress: "",
    shopDescription: "",
    status: "ACTIVE",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadShops();
  }, []);

  async function loadShops() {
    setLoading(true);
    setError("");
    try {
      const list = await listShopsForManager();
      setShops(list ?? []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      const created = await createShop({
        ownerId: user.userId,
        shopName: formData.shopName,
        shopAddress: formData.shopAddress,
        shopDescription: formData.shopDescription,
        status: formData.status,
      });
      setShops((prev) => [...prev, created]);
      setFormData({
        shopName: "",
        shopAddress: "",
        shopDescription: "",
        status: "ACTIVE",
      });
    } catch (err) {
      if (err.fieldErrors && err.fieldErrors.length > 0) {
        setError(err.fieldErrors.map((fe) => fe.message).join(" "));
      } else {
        setError(err.message);
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <Container className="py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="text-success fw-bold mb-0">Quản lý shop</h3>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      <Row className="g-4">
        <Col lg={7}>
          <Card className="shadow-sm border-0">
            <Card.Body>
              <h5 className="text-success fw-bold mb-3">Danh sách shop</h5>
              {loading ? (
                <div className="d-flex justify-content-center py-4">
                  <Spinner animation="border" />
                </div>
              ) : shops.length === 0 ? (
                <Alert variant="light" className="border text-center mb-0">
                  Chưa có shop nào.
                </Alert>
              ) : (
                <Table responsive hover align="middle" className="mb-0">
                  <thead>
                    <tr>
                      <th>Shop</th>
                      <th>Owner ID</th>
                      <th>Địa chỉ</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    {shops.map((shop) => (
                      <tr key={shop.shopId}>
                        <td>
                          <div className="fw-semibold">{shop.shopName}</div>
                          {shop.shopDescription && (
                            <div className="small text-muted">{shop.shopDescription}</div>
                          )}
                        </td>
                        <td>{shop.ownerId}</td>
                        <td>{shop.shopAddress}</td>
                        <td>
                          <Badge bg={shop.status === "ACTIVE" ? "success" : "secondary"}>
                            {shop.status}
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </Card.Body>
          </Card>
        </Col>

        <Col lg={5}>
          <Card className="shadow-sm border-0">
            <Card.Body>
              <h5 className="text-success fw-bold mb-3">Tạo shop mới</h5>
              <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="shopName">
                  <Form.Label>Tên shop</Form.Label>
                  <Form.Control
                    name="shopName"
                    value={formData.shopName}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-3" controlId="shopAddress">
                  <Form.Label>Địa chỉ shop</Form.Label>
                  <Form.Control
                    name="shopAddress"
                    value={formData.shopAddress}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-3" controlId="shopDescription">
                  <Form.Label>Mô tả</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    name="shopDescription"
                    value={formData.shopDescription}
                    onChange={handleChange}
                  />
                </Form.Group>

                <Form.Group className="mb-4" controlId="shopStatus">
                  <Form.Label>Trạng thái</Form.Label>
                  <Form.Select name="status" value={formData.status} onChange={handleChange} required>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="BANNED">BANNED</option>
                  </Form.Select>
                </Form.Group>

                <Button type="submit" variant="success" className="w-100" disabled={saving}>
                  {saving ? <Spinner animation="border" size="sm" /> : "Lưu shop"}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}
