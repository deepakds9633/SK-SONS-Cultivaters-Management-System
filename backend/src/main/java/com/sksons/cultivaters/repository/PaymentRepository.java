package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Payment;
import com.sksons.cultivaters.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByClient(Client client);
    List<Payment> findByClientId(Long clientId);

    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payment p")
    Double getTotalPaid();

    @Query("SELECT COALESCE(SUM(p.remainingBalance), 0) FROM Payment p WHERE p.remainingBalance > 0")
    Double getTotalPending();
}
