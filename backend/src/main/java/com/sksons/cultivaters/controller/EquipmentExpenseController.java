package com.sksons.cultivaters.controller;

import com.sksons.cultivaters.entity.EquipmentExpense;
import com.sksons.cultivaters.repository.EquipmentExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipment-expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentExpenseController {

    @Autowired
    private EquipmentExpenseRepository equipmentExpenseRepository;

    @GetMapping
    public List<EquipmentExpense> getAllExpenses() {
        List<EquipmentExpense> expenses = equipmentExpenseRepository.findAll();
        // Sort by date descending
        expenses.sort((a, b) -> {
            if (a.getDate() == null || b.getDate() == null) return 0;
            return b.getDate().compareTo(a.getDate());
        });
        return expenses;
    }

    @GetMapping("/equipment/{equipmentId}")
    public List<EquipmentExpense> getExpensesByEquipment(@PathVariable String equipmentId) {
        return equipmentExpenseRepository.findByEquipmentId(equipmentId);
    }

    @PostMapping
    public ResponseEntity<EquipmentExpense> createExpense(@RequestBody EquipmentExpense expense) {
        EquipmentExpense saved = equipmentExpenseRepository.save(expense);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        Optional<EquipmentExpense> expense = equipmentExpenseRepository.findById(id);
        if (expense.isPresent()) {
            equipmentExpenseRepository.delete(expense.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
