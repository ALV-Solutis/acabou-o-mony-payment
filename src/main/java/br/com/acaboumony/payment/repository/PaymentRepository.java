package br.com.acaboumony.payment.repository;

import br.com.acaboumony.payment.model.PaymentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, UUID> {

    Page<PaymentModel> findAllByCpf(PageRequest pageRequest, String cpf);
}
