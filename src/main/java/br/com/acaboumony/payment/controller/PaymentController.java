package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Retornar pagamento por id",
            description = "Retorna os dados não sensiveis de um pagamento de acordo com o id fornecido. A resposta é um json com os dados do pagamento"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PaymentResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema)}, description = "Bad Request"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema)}, description = "Not Found"),
            @ApiResponse(responseCode = "406", content = {@Content(schema = @Schema)}, description = "Not Acceptable"),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema) }, description = "Internal Server Error")
    })
    @GetMapping("/id")
    public ResponseEntity<?> detailsPayment(@RequestHeader UUID id) {
        return ResponseEntity.ok().body(paymentService.getPaymentById(id));
    }

    @Operation(
            summary = "Retornar pagamentos por cpf",
            description = "Retorna os dados não sensiveis dos pagamentos realizados pelo cpf fornecido. A resposta é um json com os dados de cada pagamento"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PaymentResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema)}, description = "Bad Request"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema)}, description = "Not Found"),
            @ApiResponse(responseCode = "406", content = {@Content(schema = @Schema)}, description = "Not Acceptable"),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema) }, description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<?>> paymentsByCpf(@RequestParam String cpf) {
        return ResponseEntity.ok().body(paymentService.getPaymentsByCpf(cpf));
    }

    @Operation(
            summary = "Processar pagamento",
            description = "Processa e cria no banco de dados um pagamento de acordo com os dados fornecidos no json. O retorno é um json com os dados do pagamento realizado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = PaymentResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema)}, description = "Bad Request"),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema)}, description = "Not Found"),
            @ApiResponse(responseCode = "406", content = {@Content(schema = @Schema)}, description = "Not Acceptable"),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema) }, description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody @Valid PaymentRequestDto payment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(payment));
    }
}
