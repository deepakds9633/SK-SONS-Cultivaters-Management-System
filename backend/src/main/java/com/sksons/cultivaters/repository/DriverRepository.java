package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends MongoRepository<Driver, Long> {
}
