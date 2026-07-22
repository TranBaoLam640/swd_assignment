import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Container, Table, Button, Alert, Spinner, Badge, Form, InputGroup } from "react-bootstrap";
import { listAllProductsForManager } from "../services/productService";
import { getStock, increaseStock, decreaseStock, setStock } from "../../inventory_management_module/services/inventoryService";
import { formatUnitPrice } from "../../../common/utils/measure";

export default function ManagerProductListPage() {
  const [products, setProducts] = useState([]);
  const [stockByProduct, setStockByProduct] = useState({});
  const [adjustAmount, setAdjustAmount] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadProducts();
  }, []);

  async function loadProducts() {
    setLoading(true);
    setError("");
    try {
      const list = await listAllProductsForManager();
      setProducts(list ?? []);

      const stockEntries = await Promise.all(
        (list ?? []).map((p) =>
          getStock(p.productId)
            .then((inv) => [p.productId, inv.stockQuantity])
            .catch(() => [p.productId, null])
        )
      );
      setStockByProduct(Object.fromEntries(stockEntries));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function getAmount(productId) {
    return adjustAmount[productId] ?? 1;
  }

  function handleAmountChange(productId, value) {
    setAdjustAmount((prev) => ({ ...prev, [productId]: Number(value) }));
  }

  async function handleIncrease(productId) {
    setError("");
    try {
      const updated = await increaseStock(productId, getAmount(productId));
      setStockByProduct((prev) => ({ ...prev, [productId]: updated.stockQuantity }));
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDecrease(productId) {
    setError("");
    try {
      const updated = await decreaseStock(productId, getAmount(productId));
      setStockByProduct((prev) => ({ ...prev, [productId]: updated.stockQuantity }));
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleSetStock(productId) {
    setError("");
    try {
      const updated = await setStock(productId, getAmount(productId));
      setStockByProduct((prev) => ({ ...prev, [productId]: updated.stockQuantity }));
    } catch (err) {
      setError(err.message);
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
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h3 className="text-success fw-bold mb-0">Quản lý sản phẩm</h3>
        <Button as={Link} to="/manager/products/new" variant="success">
          + Tạo sản phẩm mới
        </Button>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {products.length === 0 ? (
        <Alert variant="light" className="border text-center">
          Chưa có sản phẩm nào.
        </Alert>
      ) : (
        <Table responsive hover align="middle">
          <thead>
            <tr>
              <th>Tên sản phẩm</th>
              <th>Shop</th>
              <th>Category</th>
              <th>Giá</th>
              <th>Trạng thái</th>
              <th style={{ width: 280 }}>Tồn kho</th>
              <th style={{ width: 90 }}></th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.productId}>
                <td>{product.productName}</td>
                <td>{product.shopName ?? `#${product.shopId}`}</td>
                <td>{product.categoryName ?? "-"}</td>
                <td>{formatUnitPrice(product)}</td>
                <td>
                  <Badge bg={product.isActive ? "success" : "secondary"}>
                    {product.isActive ? "Đang bán" : "Đã ẩn"}
                  </Badge>
                </td>
                <td>
                  <div className="d-flex align-items-center gap-2">
                    <strong>
                      {stockByProduct[product.productId] ?? "-"}
                    </strong>
                    <InputGroup size="sm" style={{ width: 190 }}>
                      <Form.Control
                        type="number"
                        min={0}
                        value={getAmount(product.productId)}
                        onChange={(e) => handleAmountChange(product.productId, e.target.value)}
                      />
                      <Button variant="outline-success" onClick={() => handleIncrease(product.productId)}>
                        +
                      </Button>
                      <Button variant="outline-danger" onClick={() => handleDecrease(product.productId)}>
                        -
                      </Button>
                      <Button variant="outline-secondary" onClick={() => handleSetStock(product.productId)}>
                        Đặt lại
                      </Button>
                    </InputGroup>
                  </div>
                </td>
                <td>
                  <Button
                    as={Link}
                    to={`/manager/products/${product.productId}/edit`}
                    size="sm"
                    variant="outline-primary"
                  >
                    Sửa
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
}
