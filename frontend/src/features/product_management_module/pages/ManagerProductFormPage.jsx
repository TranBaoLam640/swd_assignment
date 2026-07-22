import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { listCategories } from "../services/categoryService";
import { getProduct, createProduct, updateProduct } from "../services/productService";
import { listShopsForManager } from "../services/shopService";

export default function ManagerProductFormPage() {
  const { productId } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(productId);

  const [formData, setFormData] = useState({
    shopId: "",
    categoryId: "",
    productName: "",
    description: "",
    price: "",
    imageUrl: "",
    isActive: true,
  });
  const [shops, setShops] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadShops();
    loadCategories();
    if (isEdit) {
      loadProduct();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [productId]);

  async function loadShops() {
    try {
      const list = await listShopsForManager();
      setShops(list ?? []);
      if (!isEdit && (list ?? []).length > 0) {
        setFormData((prev) => ({ ...prev, shopId: prev.shopId || String(list[0].shopId) }));
      }
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadCategories() {
    try {
      const list = await listCategories();
      setCategories(list ?? []);
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadProduct() {
    setLoading(true);
    setError("");
    try {
      const product = await getProduct(productId);
      setFormData({
        shopId: product.shopId ?? "",
        categoryId: product.categoryId ?? "",
        productName: product.productName ?? "",
        description: product.description ?? "",
        price: product.price ?? "",
        imageUrl: product.imageUrl ?? "",
        isActive: product.isActive,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  function handleChange(e) {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({ ...prev, [name]: type === "checkbox" ? checked : value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      if (isEdit) {
        await updateProduct(productId, {
          categoryId: formData.categoryId ? Number(formData.categoryId) : null,
          productName: formData.productName,
          description: formData.description,
          price: Number(formData.price),
          imageUrl: formData.imageUrl,
          isActive: formData.isActive,
        });
      } else {
        await createProduct({
          shopId: Number(formData.shopId),
          categoryId: formData.categoryId ? Number(formData.categoryId) : null,
          productName: formData.productName,
          description: formData.description,
          price: Number(formData.price),
          imageUrl: formData.imageUrl,
        });
      }
      navigate("/manager/products");
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

  if (loading) {
    return (
      <Container className="d-flex justify-content-center py-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="py-4" style={{ maxWidth: 560 }}>
      <Card className="shadow-sm border-0 p-4">
        <Card.Body>
          <h4 className="text-success fw-bold mb-4">
            {isEdit ? "Sửa sản phẩm" : "Tạo sản phẩm mới"}
          </h4>

          {error && <Alert variant="danger">{error}</Alert>}

          <Form onSubmit={handleSubmit}>
            {!isEdit && (
              <Form.Group className="mb-3" controlId="productShopId">
                <Form.Label>Shop</Form.Label>
                <Form.Select
                  name="shopId"
                  value={formData.shopId}
                  onChange={handleChange}
                  required
                >
                  <option value="" disabled>
                    Chọn shop
                  </option>
                  {shops.map((shop) => (
                    <option key={shop.shopId} value={shop.shopId}>
                      {shop.shopName}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            )}

            <Form.Group className="mb-3" controlId="productCategoryId">
              <Form.Label>Category</Form.Label>
              <Form.Select name="categoryId" value={formData.categoryId} onChange={handleChange}>
                <option value="">Không chọn category</option>
                {categories.map((category) => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.categoryName}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3" controlId="productName">
              <Form.Label>Tên sản phẩm</Form.Label>
              <Form.Control
                name="productName"
                value={formData.productName}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="productDescription">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={formData.description}
                onChange={handleChange}
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="productPrice">
              <Form.Label>Giá bán</Form.Label>
              <Form.Control
                type="number"
                min={0}
                step="0.01"
                name="price"
                value={formData.price}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-3" controlId="productImageUrl">
              <Form.Label>Ảnh sản phẩm (URL)</Form.Label>
              <Form.Control name="imageUrl" value={formData.imageUrl} onChange={handleChange} />
            </Form.Group>

            {isEdit && (
              <Form.Group className="mb-4" controlId="productIsActive">
                <Form.Check
                  type="switch"
                  label="Đang bán"
                  name="isActive"
                  checked={formData.isActive}
                  onChange={handleChange}
                />
              </Form.Group>
            )}

            <Button type="submit" variant="success" className="w-100" disabled={saving}>
              {saving ? <Spinner animation="border" size="sm" /> : "Lưu"}
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
}
