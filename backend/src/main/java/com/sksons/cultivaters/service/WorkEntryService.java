package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.*;
import com.sksons.cultivaters.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class WorkEntryService {

    @Autowired
    private WorkEntryRepository workEntryRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DriverRepository driverRepository;


    public List<WorkEntry> getAllWorkEntries() {
        return workEntryRepository.findAll();
    }

    public List<WorkEntry> getWorkEntriesByClientId(Long clientId) {
        return workEntryRepository.findByClientId(clientId);
    }

    public Optional<WorkEntry> getWorkEntryById(Long id) {
        return workEntryRepository.findById(id);
    }

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public WorkEntry saveWorkEntry(WorkEntry workEntry) {
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

        // Calculate total minutes from start/end time
        if (workEntry.getStartTime() != null && workEntry.getEndTime() != null) {
            Duration duration = Duration.between(workEntry.getStartTime(), workEntry.getEndTime());
            long minutes = duration.toMinutes();
            if (minutes < 0) minutes += 24 * 60; // handle overnight
            workEntry.setTotalMinutes((int) minutes);

            // Auto-calculate cost unless it's manual (Loader/Tipper)
            if (!Boolean.TRUE.equals(workEntry.getIsManualCost())) {
                if (workEntry.getVehicle() != null) {
                    double rate = workEntry.getVehicle().getMinuteCharge();
                    workEntry.setTotalCost(minutes * rate);
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

    @Transactional
    public WorkEntry updateWorkEntry(Long id, WorkEntry workEntryDetails) {
        WorkEntry workEntry = workEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("WorkEntry not found with id: " + id));
        workEntry.setClient(workEntryDetails.getClient());
        workEntry.setVehicle(workEntryDetails.getVehicle());
        workEntry.setDriver(workEntryDetails.getDriver());
        workEntry.setWorkDate(workEntryDetails.getWorkDate());
        workEntry.setStartTime(workEntryDetails.getStartTime());
        workEntry.setEndTime(workEntryDetails.getEndTime());
        workEntry.setIsManualCost(workEntryDetails.getIsManualCost());
        workEntry.setTotalCost(workEntryDetails.getTotalCost());
        workEntry.setNotes(workEntryDetails.getNotes());
        return saveWorkEntry(workEntry);
    }

    @Transactional
    public void deleteWorkEntry(Long id) {
        WorkEntry workEntry = workEntryRepository.findById(id).orElse(null);
        workEntryRepository.deleteById(id);
        if (workEntry != null && workEntry.getClient() != null) {
            updateClientTotalCharge(workEntry.getClient().getId());
        }
    }

    public Double getTotalIncome() {
        return workEntryRepository.getTotalIncome();
    }

    public Long getTotalWorkCount() {
        return workEntryRepository.getTotalWorkCount();
    }


    private void updateClientTotalCharge(Long clientId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            List<WorkEntry> entries = workEntryRepository.findByClientId(clientId);
            double total = entries.stream()
                .mapToDouble(e -> e.getTotalCost() != null ? e.getTotalCost() : 0.0)
                .sum();
            client.setTotalCharge(total);
            client.setPendingBalance(total - client.getTotalPaid());
            clientRepository.save(client);
        });
    }
}
