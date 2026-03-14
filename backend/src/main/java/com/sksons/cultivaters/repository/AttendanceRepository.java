package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, Long> {
    List<Attendance> findByDriverId(Long driverId);
    List<Attendance> findByDate(LocalDate date);
    Optional<Attendance> findByDriverIdAndDate(Long driverId, LocalDate date);
    List<Attendance> findByDriverIdAndDateBetween(Long driverId, LocalDate startDate, LocalDate endDate);
}
