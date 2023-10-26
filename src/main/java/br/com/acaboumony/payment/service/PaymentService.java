package br.com.acaboumony.payment.service;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.enums.PaymentStatus;
import br.com.acaboumony.payment.mapper.GenericMapper;
import br.com.acaboumony.payment.model.PaymentModel;
import br.com.acaboumony.payment.producer.PaymentProducer;
import br.com.acaboumony.payment.repository.PaymentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private GenericMapper<PaymentResponseDto, PaymentModel> paymentReqMapper;

    public PaymentService(PaymentRepository paymentRepository, PaymentProducer paymentProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
    }

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequestDto) {
        PaymentModel paymentModel = new PaymentModel();
        BeanUtils.copyProperties(paymentRequestDto, paymentModel);
        try {
            paymentModel.setPaymentStatus(PaymentStatus.CONFIRMED);
            paymentRepository.save(paymentModel);
        } catch (Exception e) {
            paymentModel.setPaymentStatus(PaymentStatus.CANCELED);
            paymentRepository.save(paymentModel);
        }
        finally {
            paymentProducer.publishMessageEmail(paymentModel);
        }
        return getPaymentById(paymentModel.getPaymentId());
    }

    public PaymentResponseDto getPaymentById(UUID paymentId) {
        PaymentModel paymentModel = paymentRepository.findById(paymentId).orElseThrow();
        PaymentResponseDto payment = paymentReqMapper.mapModelToDto(paymentModel, PaymentResponseDto.class);
        payment.setNumber(formatNumberCard(payment.getNumber()));
        return payment;
    }

    public String formatNumberCard(String numberCard) {
        return numberCard.replace(numberCard.substring(4, 12), "********");
    }
}
