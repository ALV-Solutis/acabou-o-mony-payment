package br.com.acaboumony.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(BigDecimal value,
                         String name,
                         String number,
                         String dueDate,
                         String code,
                         UUID userId,
                         String nameUser,
                         String email) {
}
