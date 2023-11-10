package br.com.acaboumony.payment.service;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.enums.PaymentStatus;
import br.com.acaboumony.payment.exception.PaymentProcessingException;
import br.com.acaboumony.payment.mapper.GenericMapper;
import br.com.acaboumony.payment.model.PaymentModel;
import br.com.acaboumony.payment.producer.PaymentProducer;
import br.com.acaboumony.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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

            paymentRepository.save(paymentModel);
            paymentProducer.publishMessageEmail(paymentModel);

        } catch (Exception e) {

            paymentModel.setPaymentStatus(PaymentStatus.CANCELED);
            throw new PaymentProcessingException("Pagamento Cancelado", e);

        } finally {
            paymentRepository.save(paymentModel);
        }

        return getPaymentById(paymentModel.getPaymentId());
    }

    public PaymentResponseDto getPaymentById(UUID paymentId) {
        PaymentModel paymentModel = paymentRepository.findById(paymentId).orElseThrow();
        PaymentResponseDto payment = paymentResMapper.mapModelToDto(paymentModel, PaymentResponseDto.class);
        payment.setNumber(formatNumberCard(payment.getNumber()));
        return payment;
    }

    public List<PaymentResponseDto> getPaymentsByCpf(String cpf) {
        List<PaymentModel> payments = paymentRepository.findAllByCpf(cpf)
                .orElseThrow(NoSuchElementException::new);
        if (payments.isEmpty()) {
            throw new NoSuchElementException();
        }
        payments.forEach(payment -> payment.setNumber(formatNumberCard(payment.getNumber())));
        return payments.stream().map(
                payment -> paymentResMapper.mapModelToDto(payment, PaymentResponseDto.class)).toList();
    }

    private String formatNumberCard(String numberCard) {
        return numberCard.replace(numberCard.substring(4, 12), "********");
    }
}
