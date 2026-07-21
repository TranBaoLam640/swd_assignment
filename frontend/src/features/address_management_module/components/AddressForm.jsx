import { useState } from "react";
import { Form, Button, Spinner, Alert } from "react-bootstrap";

/**
 * Shared "add/edit shipping address" form. Used both:
 *  - forced/blocking, when the customer has zero saved addresses yet
 *    (forceDefault=true hides the "đặt làm mặc định" checkbox since the
 *    backend makes the first address default automatically anyway), and
 *  - inline on Checkout, when the customer already has at least one address
 *    and is adding another one (forceDefault=false, checkbox shown).
 */
export default function AddressForm({
  initialValue = null,
  forceDefault = false,
  submitLabel = "Lưu địa chỉ",
  onCancel = null,
  onSubmit,
}) {
  const [values, setValues] = useState({
    receiverName: initialValue?.receiverName ?? "",
    receiverPhone: initialValue?.receiverPhone ?? "",
    province: initialValue?.province ?? "",
    district: initialValue?.district ?? "",
    ward: initialValue?.ward ?? "",
    specificAddress: initialValue?.specificAddress ?? "",
    defaultAddress: initialValue?.defaultAddress ?? forceDefault,
  });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  function set(field, value) {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (
      !values.receiverName.trim() ||
      !values.receiverPhone.trim() ||
      !values.province.trim() ||
      !values.district.trim() ||
      !values.ward.trim()
    ) {
      setError("Vui lòng điền đầy đủ Họ tên, Số điện thoại, Tỉnh/Thành phố, Quận/Huyện và Phường/Xã.");
      return;
    }

    setSaving(true);
    try {
      await onSubmit(values);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <Form onSubmit={handleSubmit}>
      {error && <Alert variant="danger">{error}</Alert>}

      <Form.Group className="mb-2">
        <Form.Label>Họ tên người nhận</Form.Label>
        <Form.Control
          value={values.receiverName}
          onChange={(e) => set("receiverName", e.target.value)}
          placeholder="Nguyễn Văn A"
        />
      </Form.Group>

      <Form.Group className="mb-2">
        <Form.Label>Số điện thoại</Form.Label>
        <Form.Control
          value={values.receiverPhone}
          onChange={(e) => set("receiverPhone", e.target.value)}
          placeholder="09xxxxxxxx"
        />
      </Form.Group>

      <Form.Group className="mb-2">
        <Form.Label>Tỉnh/Thành phố</Form.Label>
        <Form.Control
          value={values.province}
          onChange={(e) => set("province", e.target.value)}
          placeholder="Hà Nội"
        />
      </Form.Group>

      <Form.Group className="mb-2">
        <Form.Label>Quận/Huyện</Form.Label>
        <Form.Control
          value={values.district}
          onChange={(e) => set("district", e.target.value)}
          placeholder="Hà Đông"
        />
      </Form.Group>

      <Form.Group className="mb-2">
        <Form.Label>Phường/Xã</Form.Label>
        <Form.Control
          value={values.ward}
          onChange={(e) => set("ward", e.target.value)}
          placeholder="La Khê"
        />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Địa chỉ cụ thể</Form.Label>
        <Form.Control
          value={values.specificAddress}
          onChange={(e) => set("specificAddress", e.target.value)}
          placeholder="Số nhà, tên đường, tòa nhà..."
        />
      </Form.Group>

      {!forceDefault && (
        <Form.Check
          type="checkbox"
          id="defaultAddressCheck"
          label="Đặt làm địa chỉ mặc định"
          checked={values.defaultAddress}
          onChange={(e) => set("defaultAddress", e.target.checked)}
          className="mb-3"
        />
      )}

      <div className="d-flex gap-2">
        <Button type="submit" variant="success" disabled={saving}>
          {saving ? <Spinner animation="border" size="sm" /> : submitLabel}
        </Button>
        {onCancel && (
          <Button type="button" variant="outline-secondary" disabled={saving} onClick={onCancel}>
            Hủy
          </Button>
        )}
      </div>
    </Form>
  );
}
