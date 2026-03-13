package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Attendance;

import com.sksons.cultivaters.repository.AttendanceRepository;
import com.sksons.cultivaters.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private DriverRepository driverRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendanceByDriverId(Long driverId) {
        return attendanceRepository.findByDriverId(driverId);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Transactional
    public Attendance saveAttendance(Attendance attendance) {
        // If present, calculate salary; if not, salary = 0
        if (!Boolean.TRUE.equals(attendance.getPresent())) {
            attendance.setSalaryForDay(0.0);
        } else if (attendance.getSalaryForDay() == null) {
            // Use driver's daily salary
            if (attendance.getDriver() != null) {
                driverRepository.findById(attendance.getDriver().getId()).ifPresent(driver -> {
                    attendance.setSalaryForDay(driver.getDailySalary());
                });
            }
        }

        // pending = salary - paid
        double salary = attendance.getSalaryForDay() != null ? attendance.getSalaryForDay() : 0.0;
        double paid = attendance.getPaidAmount() != null ? attendance.getPaidAmount() : 0.0;
        attendance.setPendingAmount(Math.max(0, salary - paid));

        Attendance saved = attendanceRepository.save(attendance);

        // Update driver totals
        if (saved.getDriver() != null) {
            updateDriverSalaryTotals(saved.getDriver().getId());
        }

        return saved;
    }

    @Transactional
    public Attendance updateAttendance(Long id, Attendance attendanceDetails) {
        Attendance existing = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found with id: " + id));
        existing.setDriver(attendanceDetails.getDriver());
        existing.setDate(attendanceDetails.getDate());
        existing.setPresent(attendanceDetails.getPresent());
        existing.setSalaryForDay(attendanceDetails.getSalaryForDay());
        existing.setPaidAmount(attendanceDetails.getPaidAmount());
        existing.setRemarks(attendanceDetails.getRemarks());
        return saveAttendance(existing);
    }

    @Transactional
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        attendanceRepository.deleteById(id);
        if (attendance != null && attendance.getDriver() != null) {
            updateDriverSalaryTotals(attendance.getDriver().getId());
        }
    }

    public Double getTotalPendingSalary() {
        return attendanceRepository.getTotalPendingSalary();
    }

    public Double getTotalSalaryPaid() {
        return attendanceRepository.getTotalSalaryPaid();
    }

    private void updateDriverSalaryTotals(Long driverId) {
        driverRepository.findById(driverId).ifPresent(driver -> {
            List<Attendance> records = attendanceRepository.findByDriverId(driverId);
            double totalEarned = records.stream()
                .mapToDouble(a -> a.getSalaryForDay() != null ? a.getSalaryForDay() : 0.0)
                .sum();
            double totalPaid = records.stream()
                .mapToDouble(a -> a.getPaidAmount() != null ? a.getPaidAmount() : 0.0)
                .sum();
            driver.setTotalSalaryEarned(totalEarned);
            driver.setTotalSalaryPaid(totalPaid);
            driver.setPendingSalary(Math.max(0, totalEarned - totalPaid));
            driverRepository.save(driver);
        });
    }
}
