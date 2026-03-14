package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, Long> {
}
