package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@SecurityRequirement(name = "bearer-key")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/id")
    public ResponseEntity<?> detailsPayment(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(paymentService.getPaymentById(id));
    }

    @GetMapping
    public ResponseEntity<List<?>> paymentsByCpf(@RequestParam String cpf) {
        return ResponseEntity.ok().body(paymentService.getPaymentsByCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody @Valid PaymentRequestDto payment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(payment));
    }
}
