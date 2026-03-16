package com.sksons.cultivaters.controller;

import com.sksons.cultivaters.entity.Enquiry;
import com.sksons.cultivaters.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepository;

    // GET all enquiries (for admin dashboard)
    @GetMapping
    public List<Enquiry> getAllEnquiries() {
        // Find all and sort by submissionDate descending using custom sorting or just return all
        // MongoRepository findAll() doesn't sort by default, but we can rely on ID insertion order 
        // or just return the list as is for now.
        List<Enquiry> enquiries = enquiryRepository.findAll();
        enquiries.sort((a, b) -> {
            if (a.getSubmissionDate() == null || b.getSubmissionDate() == null) return 0;
            return b.getSubmissionDate().compareTo(a.getSubmissionDate());
        });
        return enquiries;
    }

    // POST a new enquiry (from landing page contact form)
    @PostMapping
    public ResponseEntity<Enquiry> createEnquiry(@RequestBody Enquiry enquiry) {
        if (enquiry.getSubmissionDate() == null) {
            enquiry.setSubmissionDate(LocalDateTime.now());
        }
        Enquiry savedEnquiry = enquiryRepository.save(enquiry);
        return new ResponseEntity<>(savedEnquiry, HttpStatus.CREATED);
    }

    // DELETE an enquiry
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable String id) {
        Optional<Enquiry> enquiry = enquiryRepository.findById(id);
        if (enquiry.isPresent()) {
            enquiryRepository.delete(enquiry.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
