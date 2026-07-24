package com.fulfillment.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.fulfillment.dto.request.PaymentRequest;
import com.fulfillment.dto.request.RefundRequest;
import com.fulfillment.dto.response.PaymentResponse;
import com.fulfillment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaymentResolver {

    private final PaymentService paymentService;

    @MutationMapping
    public PaymentResponse makePayment(@Argument @Valid PaymentRequest paymentRequest) {
        return paymentService.makePayment(paymentRequest);
    }

    @MutationMapping
    public PaymentResponse refundPayment(@Argument @Valid RefundRequest refundRequest) {
        return paymentService.refundPayment(refundRequest);
    }

    @QueryMapping
    public PaymentResponse paymentById(@Argument Long paymentId) {
        return paymentService.paymentById(paymentId);
    }

    @QueryMapping
    public PaymentResponse paymentByOrderId(@Argument Long orderId) {
        return paymentService.paymentByOrderId(orderId);
    }

    @QueryMapping
    public List<PaymentResponse> payments() {
        return paymentService.payments();
    }

}