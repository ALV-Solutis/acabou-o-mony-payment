package br.com.acaboumony.payment.service;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.enums.PaymentStatus;
import br.com.acaboumony.payment.mapper.GenericMapper;
import br.com.acaboumony.payment.model.PaymentModel;
import br.com.acaboumony.payment.producer.PaymentProducer;
import br.com.acaboumony.payment.repository.PaymentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final GenericMapper<PaymentResponseDto, PaymentModel> paymentReqMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentProducer paymentProducer, GenericMapper<PaymentResponseDto, PaymentModel> paymentReqMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        this.paymentReqMapper = paymentReqMapper;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {
        PaymentModel paymentModel = new PaymentModel();
        BeanUtils.copyProperties(paymentRequestDto, paymentModel);
        paymentModel.setPaymentDate(LocalDateTime.now());
        try {
            paymentModel.setPaymentStatus(PaymentStatus.CONFIRMED);
        } catch (Exception e) {
            paymentModel.setPaymentStatus(PaymentStatus.CANCELED);
        }
        paymentRepository.save(paymentModel);
        paymentProducer.publishMessageEmail(paymentModel);
        return getPaymentById(paymentModel.getPaymentId());
    }

    public PaymentResponseDto getPaymentById(UUID paymentId) {
        PaymentModel paymentModel = paymentRepository.findById(paymentId).orElseThrow();
        PaymentResponseDto payment = paymentReqMapper.mapModelToDto(paymentModel, PaymentResponseDto.class);
        payment.setNumber(formatNumberCard(payment.getNumber()));
        return payment;
    }

    private String formatNumberCard(String numberCard) {
        return numberCard.replace(numberCard.substring(4, 12), "********");
    }
}
