package com.freshmart.backend.dto.request.address_management_module;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Used for both "create address" and "update address" — every shipping field is
 * required per the SDS Address table (NN) except specificAddress.
 *
 * NOTE: the boolean field is intentionally named "defaultAddress" rather than
 * "isDefault" — Lombok strips the "is" prefix from boolean setters, which makes
 * Jackson bind the JSON property as "default" instead of "isDefault" and is a
 * common source of silent bugs. "defaultAddress" avoids that trap entirely. */
@Getter
@Setter
public class AddressRequest {

    @NotBlank(message = "Họ tên người nhận không được để trống.")
    private String receiverName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống.")
    private String receiverPhone;

    @NotBlank(message = "Vui lòng chọn Tỉnh/Thành phố.")
    private String province;

    @NotBlank(message = "Vui lòng chọn Quận/Huyện.")
    private String district;

    @NotBlank(message = "Vui lòng chọn Phường/Xã.")
    private String ward;

    private String specificAddress;

    /** If true — or this is the customer's very first address — every other address
     * of this customer is unset as default so exactly one stays default. */
    private boolean defaultAddress;
}
