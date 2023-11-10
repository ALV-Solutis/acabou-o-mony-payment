package br.com.acaboumony.payment.controller;

import br.com.acaboumony.payment.dto.PaymentRequestDto;
import br.com.acaboumony.payment.dto.PaymentResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

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
    @DisplayName("Devolver código HTTP 200 e um body quando existir")
    void detailsPayment() {

    }

    @Test
    @WithMockUser
    @DisplayName("Devolver código HTTP 500 quando não for informado cpf no parametro.")
    void noCpfPaymentsByCpf() throws Exception {

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
    @DisplayName("Devolver código HTTP 400 quando o corpo da requisição estiver vazio")
    void noBodyProcessPayment() throws Exception {

        var response = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}