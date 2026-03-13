package com.sksons.cultivaters.controller;

import com.sksons.cultivaters.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired private VehicleService vehicleService;
    @Autowired private ClientService clientService;
    @Autowired private WorkEntryService workEntryService;
    @Autowired private PaymentService paymentService;
    @Autowired private DriverService driverService;
    @Autowired private AttendanceService attendanceService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVehicles", vehicleService.getTotalVehicles());
        stats.put("totalClients", clientService.getTotalClients());
        stats.put("totalWorkEntries", workEntryService.getTotalWorkCount());
        stats.put("totalDrivers", driverService.getTotalDrivers());
        stats.put("totalIncome", workEntryService.getTotalIncome());
        stats.put("totalPaid", paymentService.getTotalPaid());
        stats.put("totalPending", paymentService.getTotalPending());
        stats.put("pendingSalary", attendanceService.getTotalPendingSalary());
        return stats;
    }

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", "sk-sons-admin-token");
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
        }
        return response;
    }
}
