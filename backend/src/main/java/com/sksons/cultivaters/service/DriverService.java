package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Driver;
import com.sksons.cultivaters.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver driverDetails) {
        Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        driver.setName(driverDetails.getName());
        driver.setPhone(driverDetails.getPhone());
        driver.setLicenseNumber(driverDetails.getLicenseNumber());
        driver.setAddress(driverDetails.getAddress());
        driver.setDailySalary(driverDetails.getDailySalary());
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

    public long getTotalDrivers() {
        return driverRepository.count();
    }
}
