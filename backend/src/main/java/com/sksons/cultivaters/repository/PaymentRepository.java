package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Payment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, Long> {
    List<Payment> findByClientId(Long clientId);
}
