package com.freshmart.backend.service.impl.product_management_module;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freshmart.backend.common.exception.BusinessException;
import com.freshmart.backend.common.exception.ResourceNotFoundException;
import com.freshmart.backend.data_access.entity.Order;
import com.freshmart.backend.data_access.entity.ProductReview;
import com.freshmart.backend.data_access.entity.User;
import com.freshmart.backend.data_access.repository.authentication_and_user_account.UserRepository;
import com.freshmart.backend.data_access.repository.order_management_module.OrderItemRepository;
import com.freshmart.backend.data_access.repository.order_management_module.OrderRepository;
import com.freshmart.backend.data_access.repository.product_management_module.ProductReviewRepository;
import com.freshmart.backend.dto.request.product_management_module.CreateProductReviewRequest;
import com.freshmart.backend.dto.response.product_management_module.ProductReviewResponse;
import com.freshmart.backend.enums.order_management_module.OrderStatus;
import com.freshmart.backend.enums.order_management_module.PaymentMethod;
import com.freshmart.backend.enums.payment_management_module.PaymentStatus;
import com.freshmart.backend.mapper.product_management_module.ProductReviewMapper;
import com.freshmart.backend.service.interfaces.product_management_module.ProductReviewService;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductReviewMapper reviewMapper;

    public ProductReviewServiceImpl(ProductReviewRepository reviewRepository,
                                    OrderRepository orderRepository,
                                    OrderItemRepository orderItemRepository,
                                    UserRepository userRepository,
                                    ProductReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ProductReviewResponse> listProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(review -> reviewMapper.toResponse(review, getReviewerName(review.getOrderId())))
                .toList();
    }

    @Override
    public List<ProductReviewResponse> listMyOrderReviews(Long customerId, Long orderId) {
        validateOwnedOrder(customerId, orderId);
        return reviewRepository.findByOrderId(orderId).stream()
                .map(review -> reviewMapper.toResponse(review, getReviewerName(review.getOrderId())))
                .toList();
    }

    @Override
    @Transactional
    public ProductReviewResponse createReview(Long customerId, CreateProductReviewRequest request) {
        Order order = validateOwnedOrder(customerId, request.getOrderId());
        validateReviewable(order);
        validateOrderContainsProduct(order.getId(), request.getProductId());

        if (reviewRepository.existsByOrderIdAndProductId(order.getId(), request.getProductId())) {
            throw new BusinessException("This product has already been reviewed for this order");
        }

        ProductReview review = new ProductReview();
        review.setOrderId(order.getId());
        review.setProductId(request.getProductId());
        review.setRating(request.getRating());
        review.setComment(blankToNull(request.getComment()));
        review.setImageUrl(blankToNull(request.getImageUrl()));
        review.setVideoUrl(blankToNull(request.getVideoUrl()));

        return reviewMapper.toResponse(reviewRepository.save(review), getReviewerName(order.getId()));
    }

    private Order validateOwnedOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));
        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException(403, "You do not have permission to review this order");
        }
        return order;
    }

    private void validateReviewable(Order order) {
        boolean isConfirmed = order.getStatus() == OrderStatus.CONFIRMED;
        boolean isPaidOnline = order.getPaymentMethod() == PaymentMethod.ONLINE
                && order.getPaymentStatus() == PaymentStatus.SUCCESS;
        boolean isConfirmedCod = order.getPaymentMethod() == PaymentMethod.COD;

        if (!isConfirmed || (!isPaidOnline && !isConfirmedCod)) {
            throw new BusinessException("You can only review products after the order is paid or confirmed");
        }
    }

    private void validateOrderContainsProduct(Long orderId, Long productId) {
        boolean found = orderItemRepository.findByOrderId(orderId).stream()
                .anyMatch(item -> item.getProductId().equals(productId));
        if (!found) {
            throw new BusinessException(400, "This product does not belong to the selected order");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String getReviewerName(Long orderId) {
        return orderRepository.findById(orderId)
                .map(Order::getCustomerId)
                .flatMap(userRepository::findById)
                .map(User::getFullName)
                .orElse("Khách hàng");
    }
}
