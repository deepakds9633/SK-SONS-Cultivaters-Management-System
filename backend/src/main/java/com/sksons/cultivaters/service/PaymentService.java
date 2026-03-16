package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Payment;
import com.sksons.cultivaters.entity.WorkEntry;
import com.sksons.cultivaters.repository.ClientRepository;
import com.sksons.cultivaters.repository.PaymentRepository;
import com.sksons.cultivaters.repository.WorkEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Service
public class PaymentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WorkEntryRepository workEntryRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByClientId(Long clientId) {
        return paymentRepository.findByClientId(clientId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment savePayment(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(sequenceGenerator.generateSequence(Payment.SEQUENCE_NAME));
        }  
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
        Payment saved = paymentRepository.save(payment);

        // Auto-mark work entries as paid based on cumulative total paid for this client
        if (saved.getClient() != null) {
            autoMarkEntriesByTotalPaid(saved.getClient().getId());
        }
        return saved;
    }

    /**
     * Marks work entries as paid (oldest first) as long as cumulative payment covers them.
     * Unmarks entries that are no longer covered.
     */
    private void autoMarkEntriesByTotalPaid(Long clientId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            double remaining = client.getTotalPaid();
            List<WorkEntry> entries = workEntryRepository.findByClientId(clientId);
            entries.sort(Comparator.comparing(WorkEntry::getWorkDate));

            for (WorkEntry entry : entries) {
                double cost = entry.getTotalCost() != null ? entry.getTotalCost() : 0.0;
                if (remaining >= cost) {
                    entry.setIsPaid(true);
                    remaining -= cost;
                } else {
                    entry.setIsPaid(false);
                }
                workEntryRepository.save(entry);
            }
        });
    }

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

    public void deletePayment(Long id) {
        paymentRepository.findById(id).ifPresent(payment -> {
            Long clientId = payment.getClient() != null ? payment.getClient().getId() : null;
            // Reverse payment from client
            if (clientId != null) {
                clientRepository.findById(clientId).ifPresent(client -> {
                    double revertedPaid = client.getTotalPaid() - (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0);
                    client.setTotalPaid(Math.max(0, revertedPaid));
                    client.setPendingBalance(client.getTotalCharge() - Math.max(0, revertedPaid));
                    clientRepository.save(client);
                });
            }
            paymentRepository.deleteById(id);
            // Re-evaluate which entries remain paid after deletion
            if (clientId != null) {
                autoMarkEntriesByTotalPaid(clientId);
            }
        });
    }

    public Double getTotalPaid() {
        TypedAggregation<Payment> aggregation = newAggregation(Payment.class,
            group().sum("paidAmount").as("total")
        );
        AggregationResults<org.bson.Document> result = mongoTemplate.aggregate(aggregation, org.bson.Document.class);
        org.bson.Document doc = result.getUniqueMappedResult();
        if (doc != null && doc.get("total") != null) {
            return ((Number) doc.get("total")).doubleValue();
        }
        return 0.0;
    }

    public Double getTotalPending() {
        return clientRepository.findAll().stream()
            .mapToDouble(c -> c.getPendingBalance() != null ? c.getPendingBalance() : 0.0)
            .sum();
    }

}
