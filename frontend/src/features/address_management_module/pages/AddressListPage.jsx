import { useEffect, useState } from "react";
import { Container, Card, Button, Spinner, Alert, Badge } from "react-bootstrap";
import AddressForm from "../components/AddressForm";
import {
  listAddresses,
  createAddress,
  updateAddress,
  setDefaultAddress,
  deleteAddress,
} from "../services/addressService";

/**
 * "Địa chỉ của tôi" — standalone address book page (view/edit/delete/set
 * default), separate from the inline AddressSelector shown during
 * Checkout. Same backend endpoints, same "first address is always
 * default" + "picking a new default unsets every other one" rules
 * enforced by AddressServiceImpl.
 */
export default function AddressListPage() {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [busyId, setBusyId] = useState(null);

  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    setError("");
    try {
      const list = await listAddresses();
      setAddresses(list ?? []);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(values) {
    await createAddress(values);
    setShowAddForm(false);
    await load();
  }

  async function handleUpdate(addressId, values) {
    await updateAddress(addressId, values);
    setEditingId(null);
    await load();
  }

  async function handleSetDefault(addressId) {
    setError("");
    setBusyId(addressId);
    try {
      await setDefaultAddress(addressId);
      await load();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyId(null);
    }
  }

  async function handleDelete(addressId) {
    setError("");
    setBusyId(addressId);
    try {
      await deleteAddress(addressId);
      await load();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusyId(null);
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
    <Container className="py-4" style={{ maxWidth: 720 }}>
      <h3 className="text-success fw-bold mb-4">Địa chỉ của tôi</h3>

      {error && <Alert variant="danger">{error}</Alert>}

      {addresses.length === 0 && !showAddForm && (
        <Alert variant="light" className="border">
          Bạn chưa có địa chỉ giao hàng nào. Vui lòng thêm một địa chỉ để có thể đặt hàng.
        </Alert>
      )}

      {addresses.map((addr) =>
        editingId === addr.addressId ? (
          <Card key={addr.addressId} className="border-0 shadow-sm p-3 mb-3">
            <Card.Body>
              <Card.Title className="fs-6">Sửa địa chỉ</Card.Title>
              <AddressForm
                initialValue={addr}
                submitLabel="Lưu thay đổi"
                onCancel={() => setEditingId(null)}
                onSubmit={(values) => handleUpdate(addr.addressId, values)}
              />
            </Card.Body>
          </Card>
        ) : (
          <Card key={addr.addressId} className="border-0 shadow-sm p-3 mb-3">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-start">
                <div>
                  <strong>{addr.receiverName}</strong> · {addr.receiverPhone}
                  {addr.defaultAddress && <Badge bg="success" className="ms-2">Mặc định</Badge>}
                  <div className="text-muted small mt-1">
                    {[addr.specificAddress, addr.ward, addr.district, addr.province]
                      .filter(Boolean)
                      .join(", ")}
                  </div>
                </div>
              </div>
              <div className="d-flex gap-2 mt-3">
                <Button
                  variant="outline-secondary"
                  size="sm"
                  disabled={busyId === addr.addressId}
                  onClick={() => setEditingId(addr.addressId)}
                >
                  Sửa
                </Button>
                {!addr.defaultAddress && (
                  <Button
                    variant="outline-success"
                    size="sm"
                    disabled={busyId === addr.addressId}
                    onClick={() => handleSetDefault(addr.addressId)}
                  >
                    Đặt làm mặc định
                  </Button>
                )}
                <Button
                  variant="outline-danger"
                  size="sm"
                  disabled={busyId === addr.addressId}
                  onClick={() => handleDelete(addr.addressId)}
                >
                  Xóa
                </Button>
              </div>
            </Card.Body>
          </Card>
        )
      )}

      {showAddForm ? (
        <Card className="border-0 shadow-sm p-3 mb-3">
          <Card.Body>
            <Card.Title className="fs-6">Thêm địa chỉ mới</Card.Title>
            <AddressForm
              forceDefault={addresses.length === 0}
              submitLabel="Lưu địa chỉ"
              onCancel={addresses.length > 0 ? () => setShowAddForm(false) : null}
              onSubmit={handleCreate}
            />
          </Card.Body>
        </Card>
      ) : (
        <Button variant="success" onClick={() => setShowAddForm(true)}>
          + Thêm địa chỉ mới
        </Button>
      )}
    </Container>
  );
}
