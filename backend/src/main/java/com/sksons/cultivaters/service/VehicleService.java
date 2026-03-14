package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Vehicle;
import com.sksons.cultivaters.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    private final String uploadDir = "uploads/vehicles";

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            vehicle.setId(sequenceGenerator.generateSequence(Vehicle.SEQUENCE_NAME));
        }
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    public long getTotalVehicles() {
        return vehicleRepository.count();
    }

    public String uploadImage(Long id, MultipartFile file) throws IOException {
        Vehicle vehicle = getVehicleById(id);
        if (vehicle == null) throw new RuntimeException("Vehicle not found");

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        String imageUri = "/uploads/vehicles/" + fileName;
        vehicle.setImagePath(imageUri);
        vehicleRepository.save(vehicle);

        return imageUri;
    }
}
