package com.sksons.cultivaters.repository;

import com.sksons.cultivaters.entity.Attendance;
import com.sksons.cultivaters.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDriver(Driver driver);
    List<Attendance> findByDriverId(Long driverId);
    List<Attendance> findByDate(LocalDate date);
    Optional<Attendance> findByDriverIdAndDate(Long driverId, LocalDate date);

    @Query("SELECT COALESCE(SUM(a.pendingAmount), 0) FROM Attendance a WHERE a.pendingAmount > 0")
    Double getTotalPendingSalary();

    @Query("SELECT COALESCE(SUM(a.paidAmount), 0) FROM Attendance a")
    Double getTotalSalaryPaid();
}
