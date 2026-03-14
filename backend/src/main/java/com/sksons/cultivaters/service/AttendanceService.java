package com.sksons.cultivaters.service;

import com.sksons.cultivaters.entity.Attendance;
import com.sksons.cultivaters.repository.AttendanceRepository;
import com.sksons.cultivaters.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

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

    public Attendance saveAttendance(Attendance attendance) {
        if (attendance.getId() == null) {
            attendance.setId(sequenceGenerator.generateSequence(Attendance.SEQUENCE_NAME));
        }

        // Fetch full driver if ID only
        if (attendance.getDriver() != null && attendance.getDriver().getId() != null) {
            driverRepository.findById(attendance.getDriver().getId()).ifPresent(attendance::setDriver);
        }

        // If not present, salary is 0
        if (!Boolean.TRUE.equals(attendance.getPresent())) {
            attendance.setSalaryForDay(0.0);
        } else if (attendance.getSalaryForDay() == null && attendance.getDriver() != null) {
            attendance.setSalaryForDay(attendance.getDriver().getDailySalary());
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

    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        attendanceRepository.deleteById(id);
        if (attendance != null && attendance.getDriver() != null) {
            updateDriverSalaryTotals(attendance.getDriver().getId());
        }
    }

    public Double getTotalPendingSalary() {
        TypedAggregation<Attendance> aggregation = newAggregation(Attendance.class,
            match(org.springframework.data.mongodb.core.query.Criteria.where("isPaid").is(false)),
            group().sum("dailySalary").as("total")
        );
        AggregationResults<org.bson.Document> result = mongoTemplate.aggregate(aggregation, org.bson.Document.class);
        org.bson.Document doc = result.getUniqueMappedResult();
        if (doc != null && doc.get("total") != null) {
            return ((Number) doc.get("total")).doubleValue();
        }
        return 0.0;
    }

    public Double getTotalSalaryPaid() {
        TypedAggregation<Attendance> aggregation = newAggregation(Attendance.class,
            match(org.springframework.data.mongodb.core.query.Criteria.where("isPaid").is(true)),
            group().sum("dailySalary").as("total")
        );
        AggregationResults<org.bson.Document> result = mongoTemplate.aggregate(aggregation, org.bson.Document.class);
        org.bson.Document doc = result.getUniqueMappedResult();
        if (doc != null && doc.get("total") != null) {
            return ((Number) doc.get("total")).doubleValue();
        }
        return 0.0;
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
