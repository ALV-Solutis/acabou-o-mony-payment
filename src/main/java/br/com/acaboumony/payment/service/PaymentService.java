package br.com.acaboumony.payment.service;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.enums.PaymentStatus;
import br.com.acaboumony.payment.exception.PaymentProcessingException;
import br.com.acaboumony.payment.mapper.GenericMapper;
import br.com.acaboumony.payment.model.PaymentModel;
import br.com.acaboumony.payment.producer.PaymentProducer;
import br.com.acaboumony.payment.repository.PaymentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final GenericMapper<PaymentResponseDto, PaymentModel> paymentResMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentProducer paymentProducer, GenericMapper<PaymentResponseDto, PaymentModel> paymentResMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        this.paymentResMapper = paymentResMapper;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {
        PaymentModel paymentModel = new PaymentModel();

        try {
            BeanUtils.copyProperties(paymentRequestDto, paymentModel);
            paymentModel.setPaymentDate(LocalDateTime.now());
            paymentModel.setPaymentStatus(PaymentStatus.CONFIRMED);


        } catch (Exception e) {

            paymentModel.setPaymentStatus(PaymentStatus.CANCELED);
            throw new PaymentProcessingException("Pagamento Cancelado", e);

        } finally {
            paymentRepository.save(paymentModel);
            paymentProducer.publishMessageEmail(paymentModel);
        }

        return getPaymentById(paymentModel.getPaymentId());
    }

    public PaymentResponseDto getPaymentById(UUID paymentId) {
        PaymentModel paymentModel = paymentRepository.findById(paymentId).orElseThrow();
        PaymentResponseDto payment = paymentResMapper.mapModelToDto(paymentModel, PaymentResponseDto.class);
        payment.setNumber(formatNumberCard(payment.getNumber()));
        return payment;
    }

    public Page<PaymentResponseDto> getPaymentsByCpf(String cpf, Pageable pageable) {
        Page<PaymentModel> payments = paymentRepository.findAllByCpf(cpf, pageable)
                .orElseThrow(NoSuchElementException::new);
        if (payments.isEmpty()) {
            throw new NoSuchElementException();
        }
        payments.forEach(payment -> payment.setNumber(formatNumberCard(payment.getNumber())));
        return payments.map(
                payment -> paymentResMapper.mapModelToDto(payment, PaymentResponseDto.class));
    }

    private String formatNumberCard(String numberCard) {
        return numberCard.replace(numberCard.substring(4, 12), "********");
    }
}
