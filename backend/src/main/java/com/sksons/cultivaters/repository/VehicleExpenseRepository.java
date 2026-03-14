package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.VehicleExpense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleExpenseRepository extends MongoRepository<VehicleExpense, String> {
    List<VehicleExpense> findByVehicleId(String vehicleId);
}
