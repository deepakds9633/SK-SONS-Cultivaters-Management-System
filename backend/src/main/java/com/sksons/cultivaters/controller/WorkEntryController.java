package com.sksons.cultivaters.controller;

import com.sksons.cultivaters.entity.WorkEntry;
import com.sksons.cultivaters.service.WorkEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/work-entries")
@CrossOrigin(origins = "*")
public class WorkEntryController {

    @Autowired
    private WorkEntryService workEntryService;

    @GetMapping
    public List<WorkEntry> getAllWorkEntries() {
        return workEntryService.getAllWorkEntries();
    }

    @GetMapping("/client/{clientId}")
    public List<WorkEntry> getWorkEntriesByClient(@PathVariable Long clientId) {
        return workEntryService.getWorkEntriesByClientId(clientId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkEntry> getWorkEntryById(@PathVariable Long id) {
        return workEntryService.getWorkEntryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WorkEntry createWorkEntry(@RequestBody WorkEntry workEntry) {
        return workEntryService.saveWorkEntry(workEntry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkEntry> updateWorkEntry(@PathVariable Long id, @RequestBody WorkEntry workEntry) {
        try {
            return ResponseEntity.ok(workEntryService.updateWorkEntry(id, workEntry));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/toggle-paid")
    public ResponseEntity<WorkEntry> togglePaid(@PathVariable Long id) {
        try {
            WorkEntry entry = workEntryService.getWorkEntryById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
            entry.setIsPaid(!Boolean.TRUE.equals(entry.getIsPaid()));
            return ResponseEntity.ok(workEntryService.saveWorkEntry(entry));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkEntry(@PathVariable Long id) {
        workEntryService.deleteWorkEntry(id);
        return ResponseEntity.noContent().build();
    }
}
