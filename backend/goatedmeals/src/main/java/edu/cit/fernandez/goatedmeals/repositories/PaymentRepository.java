package edu.cit.fernandez.goatedmeals.repositories;

import edu.cit.fernandez.goatedmeals.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
}