package br.com.acaboumony.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentResponseDto {
    private String orderNumber;
    private BigDecimal value;
    private String cpf;
    private String number;
    private LocalDateTime paymentDate;
    private UUID userId;
    private String nameUser;
    private String email;
}
