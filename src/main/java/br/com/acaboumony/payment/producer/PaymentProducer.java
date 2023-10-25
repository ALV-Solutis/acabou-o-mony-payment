package br.com.acaboumony.payment.producer;

import br.com.acaboumony.payment.dto.EmailDto;
import br.com.acaboumony.payment.model.PaymentModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public PaymentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${broker.queue.email.name.confirmation}")
    private String routingKey;

    public void publishMessageEmail(PaymentModel paymentModel) {
        EmailDto emailDto = null;
        //criar um emailDto com os dados do paymentModel

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}
