package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Equipment;
import com.sksons.cultivaters.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        if (equipment.getId() == null) {
            equipment.setId(sequenceGenerator.generateSequence(Equipment.SEQUENCE_NAME));
        }
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment equipmentDetails) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        equipment.setName(equipmentDetails.getName());
        equipment.setHourlyCharge(equipmentDetails.getHourlyCharge());
        return equipmentRepository.save(equipment);
    }

    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
    }
}
