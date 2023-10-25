package br.com.acaboumony.payment.dto;

import java.util.UUID;

public record EmailDto(UUID paymentId,
                       String emailTo) {
}
