package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Payment;
import com.sksons.cultivaters.repository.ClientRepository;
import com.sksons.cultivaters.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByClientId(Long clientId) {
        return paymentRepository.findByClientId(clientId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public Payment savePayment(Payment payment) {
        // Calculate remaining balance based on client's totals
        if (payment.getClient() != null) {
            clientRepository.findById(payment.getClient().getId()).ifPresent(client -> {
                double newTotalPaid = client.getTotalPaid() + (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0);
                double remaining = client.getTotalCharge() - newTotalPaid;
                payment.setRemainingBalance(Math.max(0, remaining));

                // Update client's total paid and pending balance
                client.setTotalPaid(newTotalPaid);
                client.setPendingBalance(Math.max(0, remaining));
                clientRepository.save(client);
            });
        }
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment existing = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        // Reverse old payment from client before applying new one
        if (existing.getClient() != null) {
            clientRepository.findById(existing.getClient().getId()).ifPresent(client -> {
                double revertedPaid = client.getTotalPaid() - (existing.getPaidAmount() != null ? existing.getPaidAmount() : 0.0);
                client.setTotalPaid(Math.max(0, revertedPaid));
                clientRepository.save(client);
            });
        }

        existing.setClient(paymentDetails.getClient());
        existing.setWorkEntry(paymentDetails.getWorkEntry());
        existing.setPaymentDate(paymentDetails.getPaymentDate());
        existing.setPaidAmount(paymentDetails.getPaidAmount());
        existing.setPaymentMode(paymentDetails.getPaymentMode());
        existing.setRemarks(paymentDetails.getRemarks());

        return savePayment(existing);
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.findById(id).ifPresent(payment -> {
            // Reverse payment from client
            if (payment.getClient() != null) {
                clientRepository.findById(payment.getClient().getId()).ifPresent(client -> {
                    double revertedPaid = client.getTotalPaid() - (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0);
                    client.setTotalPaid(Math.max(0, revertedPaid));
                    client.setPendingBalance(client.getTotalCharge() - Math.max(0, revertedPaid));
                    clientRepository.save(client);
                });
            }
            paymentRepository.deleteById(id);
        });
    }

    public Double getTotalPaid() {
        return paymentRepository.getTotalPaid();
    }

    public Double getTotalPending() {
        return paymentRepository.getTotalPending();
    }
}
