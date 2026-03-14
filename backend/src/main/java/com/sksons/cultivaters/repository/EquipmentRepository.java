package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, Long> {
}
