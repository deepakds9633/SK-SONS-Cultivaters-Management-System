package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.*;
import com.sksons.cultivaters.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class WorkEntryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WorkEntryRepository workEntryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    public List<WorkEntry> getAllWorkEntries() {
        return workEntryRepository.findAll();
    }

    public List<WorkEntry> getWorkEntriesByClientId(Long clientId) {
        return workEntryRepository.findByClientId(clientId);
    }

    public Optional<WorkEntry> getWorkEntryById(Long id) {
        return workEntryRepository.findById(id);
    }

    public WorkEntry saveWorkEntry(WorkEntry workEntry) {
        // Assign ID for new entries
        if (workEntry.getId() == null) {
            workEntry.setId(sequenceGenerator.generateSequence(WorkEntry.SEQUENCE_NAME));
        }

        // Fetch full entities if only IDs are provided
        if (workEntry.getClient() != null && workEntry.getClient().getId() != null) {
            workEntry.setClient(clientRepository.findById(workEntry.getClient().getId()).orElse(workEntry.getClient()));
        }
        if (workEntry.getVehicle() != null && workEntry.getVehicle().getId() != null) {
            workEntry.setVehicle(vehicleRepository.findById(workEntry.getVehicle().getId()).orElse(workEntry.getVehicle()));
        }
        if (workEntry.getDriver() != null && workEntry.getDriver().getId() != null) {
            workEntry.setDriver(driverRepository.findById(workEntry.getDriver().getId()).orElse(workEntry.getDriver()));
        }
        if (workEntry.getEquipment() != null && workEntry.getEquipment().getId() != null) {
            workEntry.setEquipment(equipmentRepository.findById(workEntry.getEquipment().getId()).orElse(workEntry.getEquipment()));
        }

        // Calculate total minutes from start/end time
        if (workEntry.getStartTime() != null && workEntry.getEndTime() != null) {
            Duration duration = Duration.between(workEntry.getStartTime(), workEntry.getEndTime());
            long minutes = duration.toMinutes();
            if (minutes < 0) minutes += 24 * 60; // handle overnight
            workEntry.setTotalMinutes((int) minutes);

            // Auto-calculate cost based on equipment rate if not manual
            if (!Boolean.TRUE.equals(workEntry.getIsManualCost()) && workEntry.getEquipment() != null) {
                double hourlyRate = workEntry.getEquipment().getHourlyCharge();
                double minuteRate = hourlyRate / 60.0;
                if (hourlyRate > 0.0) {
                    workEntry.setTotalCost(Math.round(minutes * minuteRate * 100.0) / 100.0);
                } else {
                    workEntry.setTotalCost(0.0);
                }
            }
        } else if (workEntry.getStartTime() == null && workEntry.getEndTime() == null
                && workEntry.getTotalMinutes() != null && workEntry.getTotalMinutes() > 0) {
            // Direct duration path: user provided minutes without start/end times
            int minutes = workEntry.getTotalMinutes();
            if (!Boolean.TRUE.equals(workEntry.getIsManualCost()) && workEntry.getEquipment() != null) {
                double hourlyRate = workEntry.getEquipment().getHourlyCharge();
                double minuteRate = hourlyRate / 60.0;
                if (hourlyRate > 0.0) {
                    workEntry.setTotalCost(Math.round(minutes * minuteRate * 100.0) / 100.0);
                } else {
                    workEntry.setTotalCost(0.0);
                }
            }
        }

        WorkEntry saved = workEntryRepository.save(workEntry);

        // Update client total charge
        if (saved.getClient() != null) {
            updateClientTotalCharge(saved.getClient().getId());
        }

        return saved;
    }

    public WorkEntry updateWorkEntry(Long id, WorkEntry workEntryDetails) {
        WorkEntry workEntry = workEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("WorkEntry not found with id: " + id));
        workEntry.setClient(workEntryDetails.getClient());
        workEntry.setVehicle(workEntryDetails.getVehicle());
        workEntry.setDriver(workEntryDetails.getDriver());
        workEntry.setEquipment(workEntryDetails.getEquipment());
        workEntry.setWorkDate(workEntryDetails.getWorkDate());
        workEntry.setStartTime(workEntryDetails.getStartTime());
        workEntry.setEndTime(workEntryDetails.getEndTime());
        // Carry over direct duration if provided (no start/end times)
        if (workEntryDetails.getStartTime() == null && workEntryDetails.getEndTime() == null
                && workEntryDetails.getTotalMinutes() != null) {
            workEntry.setTotalMinutes(workEntryDetails.getTotalMinutes());
        }
        workEntry.setIsManualCost(workEntryDetails.getIsManualCost());
        workEntry.setTotalCost(workEntryDetails.getTotalCost());
        workEntry.setNotes(workEntryDetails.getNotes());
        return saveWorkEntry(workEntry);
    }

    public void deleteWorkEntry(Long id) {
        WorkEntry workEntry = workEntryRepository.findById(id).orElse(null);
        workEntryRepository.deleteById(id);
        if (workEntry != null && workEntry.getClient() != null) {
            updateClientTotalCharge(workEntry.getClient().getId());
        }
    }

    public Double getTotalIncome() {
        TypedAggregation<WorkEntry> aggregation = newAggregation(WorkEntry.class,
            group().sum("totalCost").as("total")
        );
        AggregationResults<org.bson.Document> result = mongoTemplate.aggregate(aggregation, org.bson.Document.class);
        org.bson.Document doc = result.getUniqueMappedResult();
        if (doc != null && doc.get("total") != null) {
            return ((Number) doc.get("total")).doubleValue();
        }
        return 0.0;
    }

    public Long getTotalWorkCount() {
        return workEntryRepository.count();
    }

    private void updateClientTotalCharge(Long clientId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            List<WorkEntry> entries = workEntryRepository.findByClientId(clientId);
            double total = entries.stream()
                .mapToDouble(e -> e.getTotalCost() != null ? e.getTotalCost() : 0.0)
                .sum();
            client.setTotalCharge(total);
            client.setPendingBalance(total - (client.getTotalPaid() != null ? client.getTotalPaid() : 0.0));
            clientRepository.save(client);
        });
    }
}
