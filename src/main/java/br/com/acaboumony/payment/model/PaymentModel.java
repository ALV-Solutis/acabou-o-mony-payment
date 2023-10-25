package br.com.acaboumony.payment.model;

import br.com.acaboumony.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;
    @NotNull
    @Positive
    private BigDecimal value;
    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;
    @NotBlank
    private String nameCard;
    @NotBlank
    @Size(max = 16)
    private String number;
    @NotBlank
    @Size(min = 5, max = 7)
    private String dueDate;
    @NotBlank
    @Size(max = 3, min = 3)
    private String code;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @NotBlank
    private UUID userId;
    @NotBlank
    private String nameUser;
    @NotBlank
    private String email;
}
