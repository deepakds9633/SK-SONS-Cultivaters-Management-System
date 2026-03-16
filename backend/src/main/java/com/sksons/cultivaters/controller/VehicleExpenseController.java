package com.sksons.cultivaters.controller;

import com.sksons.cultivaters.entity.VehicleExpense;
import com.sksons.cultivaters.repository.VehicleExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class VehicleExpenseController {

    @Autowired
    private VehicleExpenseRepository vehicleExpenseRepository;

    @GetMapping
    public List<VehicleExpense> getAllExpenses() {
        List<VehicleExpense> expenses = vehicleExpenseRepository.findAll();
        // Sort by date descending
        expenses.sort((a, b) -> {
            if (a.getDate() == null || b.getDate() == null) return 0;
            return b.getDate().compareTo(a.getDate());
        });
        return expenses;
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<VehicleExpense> getExpensesByVehicle(@PathVariable String vehicleId) {
        return vehicleExpenseRepository.findByVehicleId(vehicleId);
    }

    @PostMapping
    public ResponseEntity<VehicleExpense> createExpense(@RequestBody VehicleExpense expense) {
        VehicleExpense saved = vehicleExpenseRepository.save(expense);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        Optional<VehicleExpense> expense = vehicleExpenseRepository.findById(id);
        if (expense.isPresent()) {
            vehicleExpenseRepository.delete(expense.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
