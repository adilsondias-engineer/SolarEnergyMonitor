# Solar Energy Monitor

A Java desktop application for monitoring and visualizing solar energy production, battery status, and grid usage with real-time statistics and historical data analysis.

---

⚠️ **DISCLAIMER**: Use this project at your own risk. No guarantees are provided, and no support is available. This is a personal project shared as-is for educational and reference purposes only. Users are responsible for their own implementation, testing, and any consequences arising from use of this project.

---

## Project Information

- **Author**: Adilson Dias
- **Version**: 1.0.1-SNAPSHOT
- **Language**: Java (JDK 17+)
- **Type**: Desktop Application (Java Swing GUI)
- **License**: Provided as-is for educational purposes
- **GitHub**: <https://github.com/adilsondias-engineer>

## Overview

Solar Energy Monitor is a desktop application that integrates with real-time solar PV systems. It collects data from solar inverters, battery storage, and grid meters (via a MySQL database populated by a separate collection process) to provide insights into energy production, consumption, and financial impact. Built originally for my own home solar installation to track energy flow and cost/revenue over time.

## Features

### Real-Time Monitoring
- **Battery Status**: Current charge percentage (0-100%)
- **Charging/Discharging**: Real-time power flow with time remaining estimation
- **Solar Generation**: Instantaneous power generation in Watts
- **Grid Status**: Import/export power tracking with directional flow
- **House Consumption**: Calculated from solar + grid - battery power flow

### Data Visualization
- **Time-Series Charts**: Daily energy production and consumption trends
- **Solar Generation Tracking**: Hour-by-hour solar output visualization
- **Grid Import/Export**: Visual representation of energy sold to grid vs consumed
- **Interactive Controls**: Zoom, pan, and date range selection on charts
- **Real-time Updates**: 10-second data refresh intervals

### Financial Analysis
- **Daily Energy Breakdown**: Usage and cost per day
- **Revenue Tracking**: Solar export revenue (rate configurable)
- **Grid Cost Analysis**: Import costs (rate configurable)
- **Monthly Aggregation**: Cumulative monthly statistics and trends
- **ROI Projections**: Annual savings calculations

### Battery Management
- **Capacity Monitoring**: Configurable battery capacity tracking
- **Discharge Forecasting**: Estimated time until full discharge
- **Charge Status**: Charging/discharging power in real-time
- **Health Trends**: Historical battery performance data

## System Architecture

```
┌──────────────────────────────────────────────┐
│      Solar Inverter System                   │
│  ┌─────────────────────────────────────┐    │
│  │  Solar Panels → Inverter → Battery  │    │
│  │       + Grid Connection              │    │
│  └──────────────┬──────────────────────┘    │
└─────────────────┼──────────────────────────┘
                  │
                  ├─ Solar Power (W)
                  ├─ Battery Power (W)
                  ├─ Grid Power (W)
                  └─ Battery Capacity (%)
                  │
        ┌─────────▼─────────┐
        │   MySQL Database  │
        │    (solarusage)   │
        │   [Historical     │
        │    Data Storage]  │
        └─────────┬─────────┘
                  │
        ┌─────────▼────────────────────────┐
        │  Solar Energy Monitor Application │
        │  (Java Swing GUI)                 │
        │  ┌───────────────────────────┐   │
        │  │ Real-time Statistics      │   │
        │  │ Historical Charts         │   │
        │  │ Cost Analysis & Reports   │   │
        │  │ Battery Management        │   │
        │  └───────────────────────────┘   │
        └──────────────────────────────────┘
```

## Software Requirements

| Component                | Version       | Purpose                                 |
| ------------------------ | -------------- | ----------------------------------------- |
| **Java Development Kit** | 17 or higher  | Application runtime                     |
| **Apache Maven**         | 3.x           | Build and dependency management         |
| **MySQL Server**         | 5.7 or higher | Data persistence and historical records |
| **MySQL Connector/J**    | 8.0.30        | JDBC database connectivity              |

### Dependencies

```
<!-- Data Visualization -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.3</version>
</dependency>

<!-- Database Connectivity -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.30</version>
</dependency>

<!-- GUI Framework -->
<!-- Java Swing (built-in) -->
```

## Database Setup

### Step 1: Create Database

```sql
CREATE DATABASE solarpv;
```

### Step 2: Create Table

```sql
USE solarpv;

CREATE TABLE solarusage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dateTime DATETIME NOT NULL,
    solarpower INT,
    gridPower INT,
    batteryPower INT,
    batteryCapacity INT,
    INDEX idx_datetime (dateTime)
);
```

**Column Descriptions:**
- `id`: Unique record identifier (auto-incremented)
- `dateTime`: Timestamp of the data point (format: YYYY-MM-DD HH:MM:SS)
- `solarpower`: Solar panel output in Watts
- `gridPower`: Grid power in Watts (positive=export, negative=import)
- `batteryPower`: Battery power in Watts (positive=charging, negative=discharging)
- `batteryCapacity`: Battery charge level in percentage (0-100)
- `idx_datetime`: Index on dateTime for faster queries

### Step 3: Create Database User

**Replace the placeholders below with your own credentials — never use example values in a real deployment:**

```sql
CREATE USER 'your_db_user'@'localhost' IDENTIFIED BY 'CHANGE_ME_TO_A_STRONG_PASSWORD';
GRANT ALL PRIVILEGES ON solarpv.* TO 'your_db_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 4: Verify Connection

```
mysql -u your_db_user -p
# Should show: mysql>
```

## Installation

### Step 1: Clone Repository

```
git clone https://github.com/adilsondias-engineer/SolarEnergyMonitor.git
cd SolarEnergyMonitor
```

### Step 2: Build with Maven

```
mvn clean package
```

Build output will be in the `target/` directory.

### Step 3: Verify Build

```
ls -la target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Running the Application

### Option 1: Windows Batch File (Easiest)

```
run.bat
```

### Option 2: Maven (Development)

```
mvn exec:java -Dexec.mainClass="au.net.dias.solar.Monitor"
```

### Option 3: Direct JAR Execution

```
java -jar target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### Option 4: With Custom Memory Settings

```
java -Xmx1024m -Xms512m -jar target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Project Structure

```
SolarEnergyMonitor/
├── src/
│   └── main/
│       └── java/
│           └── au/
│               └── net/
│                   └── dias/
│                       └── solar/
│                           ├── Monitor.java          # Main application & GUI (566+ lines)
│                           └── SolarData.java        # Data model class
├── target/                                           # Build output directory
├── pom.xml                                           # Maven configuration
├── run.bat                                           # Windows launch script
└── README.md                                         # This file
```

## Configuration

### Database Connection

Edit `Monitor.java` if using non-default credentials:

```java
Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/solarpv",
    "your_db_user",
    "your_password"
);
```

**Do not commit real database credentials to source control** — for anything beyond local/personal use, move these into an external config file or environment variables instead of hardcoding them in `Monitor.java`.

### Battery Capacity

Default is a placeholder value. To modify, edit `Monitor.java`:

```java
double total_bat_kwh = YOUR_CAPACITY_KWH * 1000;  // Battery capacity in Wh
```

Set `YOUR_CAPACITY_KWH` to your actual battery capacity in kWh.

### Electricity Rates

Configure your local electricity rates in `Monitor.java` (`createDataset()` method) — these are placeholders and will vary by provider/region/plan:

**Export Rate:**
```java
dailyExport * YOUR_EXPORT_RATE  // $ per kWh, from your utility provider
```

**Import Rate:**
```java
dailyGrid * YOUR_IMPORT_RATE    // $ per kWh, from your utility provider
```

## Usage

### Starting the Application

1. Launch the application (using one of the methods above)
2. The GUI window opens displaying the current solar system state
3. Data automatically refreshes every 10 seconds from the database

### Main Display Panels

**Top-Left: Real-Time Status Panel**
- Current battery percentage
- Battery charging/discharging status
- Remaining charge/discharge time
- Solar power generation (Watts)
- Grid import/export (Watts)
- House consumption (Watts)

**Bottom-Left: Daily Statistics**
- Daily breakdown of energy usage
- Solar generation per day
- Grid import/export per day
- Daily costs and revenue

**Bottom-Right: Monthly Summary**
- Monthly aggregated statistics
- Month-to-date totals
- Monthly cost analysis
- Revenue tracking

**Center/Right: Charts**
- Time-series visualization of solar, grid, and battery power
- Interactive zoom and pan capabilities
- Date range selection

## Data Flow

```
Database Entry → Parser → Calculations → Display Updates
     ↓              ↓            ↓            ↓
[Time Series] [Power/Voltage] [Costs/ROI] [Charts & Tables]
```

1. **Read**: Query MySQL `solarusage` table at 10-second intervals
2. **Parse**: Extract solar power, grid power, battery power, battery capacity
3. **Calculate**: Daily totals, revenue, costs, battery discharge time remaining
4. **Display**: Update GUI charts, tables, and statistics

## Known Limitations

- Single database instance supported (no clustering)
- No multi-user concurrent access controls
- Chart rendering performance degrades with >1 year of data
- No built-in data export to CSV/Excel (use database directly)
- Fixed electricity rates in source (requires code modification to change)
- No alert/notification system for threshold events
- Limited to system timezone (no multi-timezone support)
- Database credentials are read directly from source in this version — fine for a personal/local setup, but should be externalized before any shared or production use

## Future Enhancements

- Move credentials/rates out of source into a config file
- Advanced analytics (consumption prediction)
- Mobile companion app
- Cloud sync/backup
- Real-time alerts (email/SMS)
- Multi-site support
- CSV/PDF export
- RESTful API for third-party integration
- Configuration UI instead of code editing

## Integration with Solar Inverters

This application reads data from a MySQL database populated by a separate data-collection process. Setup steps:

1. **Inverter Configuration**: Configure your solar inverter/collector to log data to MySQL
2. **Data Mapping**: Ensure the collector's output matches column names in the schema above
3. **Polling Interval**: 10-30 second intervals recommended
4. **Timezone**: Ensure the inverter/collector system clock is accurate

## License

This project is provided as-is without any specific license terms.

## Credits

- **Developer**: Adilson Dias
- **Framework**: Java Swing, JFreeChart
- **Database**: MySQL
- **Build Tool**: Apache Maven

## Contact

**Adilson Dias**
[GitHub](https://github.com/adilsondias-engineer)

---

*A practical desktop tool for visualizing and analyzing residential solar PV energy flow and cost/revenue over time.*
