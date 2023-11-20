package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import br.com.acaboumony.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JacksonTester<PaymentRequestDto> paymentRequestDtoJson;
    @Autowired
    private JacksonTester<PaymentResponseDto> paymentResponseDtoJson;
    @Autowired
    private JacksonTester<Page<PaymentResponseDto>> paymentsListResponseDtoJson;
    @Autowired
    private PaymentService paymentService;

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 500 quando não for informado o id no cabeçalho da requisição.")
    void detailsNoPaymentId() throws Exception {

       var response = mockMvc.perform(get("/payments/id"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 404 quando não existir pagamento com o id informado.")
    void detailsPaymentNotFound() throws Exception {

        var response = mockMvc.perform(get("/payments/id")
                        .header("id", "6d16f9d7-2a1e-4df8-a0aa-96ac4aca0830"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 200 e um body quando existir.")
    void detailsPayment() throws Exception {

        var response = mockMvc.perform(get("/payments/id")
                .header("id", "959bbc53-a5a9-49d5-b694-20fce1b38c40"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var jsonEsperado = paymentResponseDtoJson.write(paymentService.getPaymentById(UUID.fromString("959bbc53-a5a9-49d5-b694-20fce1b38c40"))
        ).getJson();

        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 500 quando não for informado cpf no parametro.")
    void noCpfParamPaymentsByCpf() throws Exception {

        var response = mockMvc.perform(get("/payments"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 404 quando não houver o registro com o cpf informado.")
    void cpfNotFoundPaymentsByCpf() throws Exception {
        String cpfRandom = String.format("%011d", new Random().nextLong() % 100000000000L);

        var response = mockMvc.perform(get("/payments")
                        .param("cpf", cpfRandom))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver códito HTTP 200 e o body quando existir")
    void cpfFoundPaymentsByCpf() throws Exception {
        var response = mockMvc.perform(get("/payments")
                        .param("cpf", "54850375841")
                        .param("size", "2")
                        .param("page", "0"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Pageable pageable = PageRequest.of(0, 2);
        var jsonEsperado = paymentsListResponseDtoJson.write(paymentService.getPaymentsByCpf("54850375841", pageable)
        ).getJson();

        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 400 quando o corpo da requisição estiver vazio")
    void noBodyProcessPayment() throws Exception {

        var response = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 200 quando o pagamento for processado")
    void processPaymentSuccess() throws Exception {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto("378532", new BigDecimal("853.5"), "47385794832",
                "Leonardo J Vend", "4385378534389364", "04/32",
                "543", UUID.fromString("959bbc53-a5a9-49d5-b694-20fce1b38c40"),
                "Leonardo Macedo", "emailteste@gmail.com");

        var response = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentRequestDtoJson.write(paymentRequestDto).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }
}