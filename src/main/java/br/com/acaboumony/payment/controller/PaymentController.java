package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.service.PaymentService;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> detailsPayment(@PathVariable UUID id) {
        return ResponseEntity.ok().body(paymentService.getPaymentById(id));
    }

//    @PostMapping
//    public ResponseEntity<?> processPayment(@RequestBody PaymentRequestDto payment) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(/*processPayment*/);
//    }
}
