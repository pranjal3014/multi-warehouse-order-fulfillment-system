package com.fulfillment.service;

import java.util.List;

import com.fulfillment.dto.request.PaymentRequest;
import com.fulfillment.dto.request.RefundRequest;
import com.fulfillment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse makePayment(PaymentRequest paymentRequest);

    PaymentResponse refundPayment(RefundRequest refundRequest);

    PaymentResponse paymentById(Long paymentId);

    PaymentResponse paymentByOrderId(Long orderId);

    List<PaymentResponse> payments();

}