package com.freshmart.backend.service.impl.address_management_module;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.data_access.entity.Address;
import com.freshmart.backend.data_access.repository.address_management_module.AddressRepository;
import com.freshmart.backend.dto.request.address_management_module.AddressRequest;
import com.freshmart.backend.dto.response.address_management_module.AddressResponse;
import com.freshmart.backend.exception.address_management_module.AddressNotFoundException;
import com.freshmart.backend.mapper.address_management_module.AddressMapper;
import com.freshmart.backend.service.interfaces.address_management_module.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponse> listAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                // Default address always shown first, then oldest-first.
                .sorted(Comparator.comparing(Address::isDefault).reversed()
                        .thenComparing(Address::getId))
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        boolean isFirstAddress = !addressRepository.existsByUserId(userId);
        // The customer's very first address is always the default — there's
        // nothing to choose between yet (matches the "first address is mandatory
        // and becomes the default" requirement).
        boolean makeDefault = isFirstAddress || request.isDefaultAddress();

        if (makeDefault) {
            addressRepository.clearDefaultForUser(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        applyRequest(address, request);
        address.setDefault(makeDefault);

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = findOwnedAddress(userId, addressId);
        applyRequest(address, request);

        if (request.isDefaultAddress() && !address.isDefault()) {
            addressRepository.clearDefaultForUser(userId);
            address.setDefault(true);
        }

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {
        Address address = findOwnedAddress(userId, addressId);
        addressRepository.clearDefaultForUser(userId);
        address.setDefault(true);
        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = findOwnedAddress(userId, addressId);
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            // Promote another address (if any) so the customer always has exactly
            // one default whenever at least one address remains.
            addressRepository.findByUserId(userId).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        addressRepository.save(next);
                    });
        }
    }

    @Override
    public boolean hasNoAddress(Long userId) {
        return !addressRepository.existsByUserId(userId);
    }

    @Override
    public AddressResponse getAddress(Long userId, Long addressId) {
        return addressMapper.toResponse(findOwnedAddress(userId, addressId));
    }

    private Address findOwnedAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        if (!address.getUserId().equals(userId)) {
            throw new AddressNotFoundException(addressId);
        }
        return address;
    }

    private void applyRequest(Address address, AddressRequest request) {
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setSpecificAddress(request.getSpecificAddress());
    }
}
