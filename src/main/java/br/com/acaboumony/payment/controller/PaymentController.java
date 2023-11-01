package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/id")
    public ResponseEntity<?> detailsPayment(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(paymentService.getPaymentById(id));
    }

    @GetMapping()
    public ResponseEntity<Page<?>> paymentsByCpf(@RequestParam String cpf, @RequestParam long page, @RequestParam long size) {
        return ResponseEntity.ok().body(paymentService.getPaymentsByCpf(page, size, cpf));
    }

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody @Valid PaymentRequestDto payment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(payment));
    }
}
