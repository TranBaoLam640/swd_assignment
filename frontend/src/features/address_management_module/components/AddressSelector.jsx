import { useState } from "react";
import { Card, Form, Button } from "react-bootstrap";
import AddressForm from "./AddressForm";
import { createAddress } from "../services/addressService";

/**
 * Embedded in Checkout. Two states:
 *  - addresses.length === 0: renders the mandatory AddressForm directly
 *    (forceDefault) — there is nothing to pick yet, so no radio list at all.
 *  - addresses.length >= 1: a radio list (default pre-selected) plus a
 *    "+ Thêm địa chỉ mới" toggle that reveals AddressForm inline.
 */
export default function AddressSelector({ addresses, selectedAddressId, onSelect, onAddressesChanged }) {
  const [showForm, setShowForm] = useState(addresses.length === 0);

  async function handleCreate(values) {
    const created = await createAddress(values);
    setShowForm(false);
    await onAddressesChanged(created.addressId);
  }

  if (addresses.length === 0) {
    return (
      <Card className="border-0 shadow-sm p-3 mb-4">
        <Card.Body>
          <Card.Title className="fs-6">Địa chỉ giao hàng</Card.Title>
          <p className="text-muted small">
            Bạn chưa có địa chỉ giao hàng nào. Vui lòng nhập địa chỉ để tiếp tục đặt hàng.
          </p>
          <AddressForm forceDefault submitLabel="Lưu địa chỉ" onSubmit={handleCreate} />
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className="border-0 shadow-sm p-3 mb-4">
      <Card.Body>
        <Card.Title className="fs-6">Địa chỉ giao hàng</Card.Title>

        {addresses.map((addr) => (
          <Form.Check
            key={addr.addressId}
            type="radio"
            name="shippingAddress"
            id={`address-${addr.addressId}`}
            className="mb-2"
            checked={selectedAddressId === addr.addressId}
            onChange={() => onSelect(addr.addressId)}
            label={
              <span>
                <strong>{addr.receiverName}</strong> · {addr.receiverPhone}
                {addr.defaultAddress && <span className="text-success ms-2">(Mặc định)</span>}
                <br />
                <span className="text-muted small">
                  {[addr.specificAddress, addr.ward, addr.district, addr.province]
                    .filter(Boolean)
                    .join(", ")}
                </span>
              </span>
            }
          />
        ))}

        {showForm ? (
          <div className="mt-3 border-top pt-3">
            <AddressForm submitLabel="Lưu địa chỉ mới" onCancel={() => setShowForm(false)} onSubmit={handleCreate} />
          </div>
        ) : (
          <Button variant="outline-success" size="sm" className="mt-2" onClick={() => setShowForm(true)}>
            + Thêm địa chỉ mới
          </Button>
        )}
      </Card.Body>
    </Card>
  );
}
