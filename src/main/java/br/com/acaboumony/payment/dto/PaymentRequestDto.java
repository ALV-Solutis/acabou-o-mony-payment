package br.com.acaboumony.payment.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDto(@NotBlank String orderNumber,
                                @NotNull @Positive BigDecimal value,
                                @NotBlank @Size(min = 11, max = 11) String cpf,
                                @NotBlank String nameCard,
                                @NotBlank @Size(max = 16) String number,
                                @NotBlank @Size(max = 5, min = 5) String dueDate,
                                @NotBlank @Size(max = 3, min = 3) String code,
                                @NotBlank UUID userId,
                                @NotBlank String nameUser,
                                @NotBlank @Email String email) {
}
