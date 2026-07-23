package com.fulfillment.service.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fulfillment.dto.request.PaymentRequest;
import com.fulfillment.dto.request.RefundRequest;
import com.fulfillment.dto.response.PaymentResponse;
import com.fulfillment.entity.Payment;
import com.fulfillment.enums.PaymentStatus;
import com.fulfillment.exception.PaymentAlreadyExistsException;
import com.fulfillment.exception.PaymentNotFoundException;
import com.fulfillment.mapper.PaymentMapper;
import com.fulfillment.repository.PaymentRepository;
import com.fulfillment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {

        paymentRepository.findByOrderId(paymentRequest.getOrderId())
                .ifPresent(payment -> {
                    throw new PaymentAlreadyExistsException(
                            "Payment already exists for Order Id : " + paymentRequest.getOrderId());
                });

        Payment payment = paymentMapper.toEntity(paymentRequest);

        payment.setTransactionId("PAY-" + UUID.randomUUID());

        payment.setPaymentStatus(PaymentStatus.PAID);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public PaymentResponse refundPayment(RefundRequest refundRequest) {

        Payment payment = paymentRepository.findById(refundRequest.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with Id : " + refundRequest.getPaymentId()));

        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Payment is already refunded.");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);

        Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(updatedPayment);
    }

    @Override
    public PaymentResponse paymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with Id : " + paymentId));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse paymentByOrderId(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for Order Id : " + orderId));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> payments() {

        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

}