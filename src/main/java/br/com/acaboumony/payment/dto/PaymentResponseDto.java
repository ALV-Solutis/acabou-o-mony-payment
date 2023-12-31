package br.com.acaboumony.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID paymentId;
    private String orderNumber;
    private BigDecimal value;
    private String cpf;
    private String number;
    private LocalDateTime paymentDate;
    private UUID userId;
    private String nameUser;
    private String email;
}
