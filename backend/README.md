# SK SONS Cultivators Management System

Agricultural Machinery Management System for SK SONS Cultivators.

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Set Up Database
```sql
-- Run schema.sql in MySQL to create the database and tables with seed vehicles:
source schema.sql
```
Or import via MySQL Workbench or HeidiSQL.

### 2. Configure Database Password
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```
Default username is `root`.

### 3. Run the Application
```bash
cd "SK SONS Cultivators Management System"
mvn spring-boot:run
```

### 4. Open the Frontend
The frontend has been completely decoupled! Do **NOT** go to `localhost:8080/login.html` anymore (that is now just the raw REST API).

Instead, open the separated frontend directly:
1. Go to the `SK SONS Cultivators Frontend` folder on your Desktop.
2. Double-click `login.html` to open it in your browser.

Login credentials:
- **Username:** `admin`
- **Password:** `admin123`

---

## Modules

| Module | URL | Description |
|--------|-----|-------------|
| Login | `/login.html` | Admin login |
| Dashboard | `/index.html` | Overview stats & quick links |
| Vehicles | `/vehicles.html` | Add/edit agricultural machines |
| Clients | `/clients.html` | Register farmers & track balances |
| Work Entries | `/work-entries.html` | Record cultivation work with auto cost |
| Payments | `/payments.html` | Track payments & pending balances |
| Drivers | `/drivers.html` | Driver profiles & salary overview |
| Attendance | `/attendance.html` | Daily attendance & salary tracking |

## Service Charge Rates

| Vehicle | Rate/Minute | Rate/Hour | Type |
|---------|------------|-----------|------|
| Rotavator | ₹20.00 | ₹1,200.00 | Auto-calculated |
| 9-Tine Cultivator | ₹16.66 | ₹999.60 | Auto-calculated |
| Half Cage Wheel | ₹16.66 | ₹999.60 | Auto-calculated |
| Loader (Tipper) | Manual | Manual | Manual entry |

## Tech Stack
- **Backend:** Spring Boot 3.2.3, Spring Data JPA, Spring Security
- **Database:** MySQL 8.0 (auto-creates schema via Hibernate)
- **Frontend:** Vanilla HTML, CSS, JavaScript
