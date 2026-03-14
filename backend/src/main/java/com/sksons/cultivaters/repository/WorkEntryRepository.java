package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.WorkEntry;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkEntryRepository extends MongoRepository<WorkEntry, Long> {
    List<WorkEntry> findByClientId(Long clientId);
}
