package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.EquipmentExpense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentExpenseRepository extends MongoRepository<EquipmentExpense, String> {
    
    List<EquipmentExpense> findByEquipmentId(String equipmentId);
}
