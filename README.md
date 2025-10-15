# Solar Energy Monitor

A Java desktop application for monitoring and visualizing solar energy production, battery status, and grid usage with real-time statistics and historical data analysis.

> **TML** stands for **Tiny Memories Laser**, a registered business (API-led Pty Ltd, ABN) that operated from 2018-2024. This Solar Energy Monitor project represents the software/data analysis division, extending TML's expertise into renewable energy system optimization and monitoring.

---

⚠️ **DISCLAIMER**: Use this project at your own risk. No guarantees are provided, and no support is available. This is a personal project shared as-is for educational and reference purposes only. Users are responsible for their own implementation, testing, and any consequences arising from use of this project.

---

## Project Information

- **Author**: Adilson Dias (API-Led Pty Ltd → Tiny Memories Laser (TML))
- **Version**: 1.0.1-SNAPSHOT
- **Language**: Java (JDK 17+)
- **Type**: Desktop Application (Java Swing GUI)
- **License**: Provided as-is for educational purposes
- **GitHub**: https://github.com/adilsondias-engineer

## Overview

Solar Energy Monitor is a comprehensive desktop application that integrates with real-time solar PV systems. The system collects data from solar inverters, battery storage, and grid meters to provide actionable insights into energy production, consumption, and financial impact. Perfect for homeowners with solar installations, energy management optimization, and cost analysis.

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
- **Revenue Tracking**: Solar export revenue at $0.067/kWh
- **Grid Cost Analysis**: Import costs at $0.19030/kWh
- **Monthly Aggregation**: Cumulative monthly statistics and trends
- **ROI Projections**: Annual savings calculations

### Battery Management
- **Capacity Monitoring**: 11.8 kWh battery capacity tracking
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

## Hardware Requirements

### System Requirements
| Component | Specification |
|-----------|---------------|
| **CPU** | Intel i5 or equivalent (minimal) |
| **RAM** | 4GB minimum, 8GB recommended |
| **Storage** | 500MB for application + database |
| **Display** | 1920x1080 or higher recommended |
| **OS** | Windows, Linux, macOS |

### Solar System Integration
| Component | Specification |
|-----------|---------------|
| **Solar Battery** | 11.8 kWh (configurable) |
| **Data Source** | MySQL database from inverter integration |
| **Data Rate** | 10-second polling interval |
| **Timezone** | System timezone (configurable) |

## Software Requirements

### Prerequisites
| Component | Version | Purpose |
|-----------|---------|---------|
| **Java Development Kit** | 17 or higher | Application runtime |
| **Apache Maven** | 3.x | Build and dependency management |
| **MySQL Server** | 5.7 or higher | Data persistence and historical records |
| **MySQL Connector/J** | 8.0.30 | JDBC database connectivity |

### Dependencies

The project uses Maven for dependency management. Key libraries:

```xml
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

```sql
CREATE USER 'solarpv'@'localhost' IDENTIFIED BY 'solarpvpw';
GRANT ALL PRIVILEGES ON solarpv.* TO 'solarpv'@'localhost';
FLUSH PRIVILEGES;
```

### Step 4: Verify Connection

```bash
mysql -u solarpv -p
# Enter password: solarpvpw
# Should show: mysql>
```

## Installation

### Step 1: Clone Repository

```bash
git clone https://github.com/adilsondias-engineer/SolarEnergyMonitor.git
cd SolarEnergyMonitor
```

### Step 2: Build with Maven

```bash
mvn clean package
```

This will:
- Download all dependencies
- Compile source code
- Run tests
- Package into executable JAR: `SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar`

Build output will be in the `target/` directory.

### Step 3: Verify Build

```bash
ls -la target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Running the Application

### Option 1: Windows Batch File (Easiest)

```bash
run.bat
```

This script automatically handles Java path and memory settings.

### Option 2: Maven (Development)

```bash
mvn exec:java -Dexec.mainClass="au.net.dias.solar.Monitor"
```

Useful for development and debugging.

### Option 3: Direct JAR Execution

```bash
java -jar target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

### Option 4: With Custom Memory Settings

```bash
java -Xmx1024m -Xms512m -jar target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
```

Parameters:
- `-Xmx1024m`: Maximum heap size (1GB)
- `-Xms512m`: Initial heap size (512MB)

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
│   ├── classes/                                      # Compiled classes
│   ├── SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar
│   └── ...
├── pom.xml                                           # Maven configuration
├── run.bat                                           # Windows launch script
└── README.md                                         # This file
```

## Configuration

### Database Connection

Edit `Monitor.java` (line 566) if using non-default credentials:

```java
Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/solarpv", 
    "solarpv", 
    "solarpvpw"
);
```

Parameters:
- `localhost:3306` - MySQL server host and port
- `solarpv` - Database name
- `"solarpv"` - Username
- `"solarpvpw"` - Password

### Battery Capacity

Default is 11.8 kWh. To modify, edit `Monitor.java` line 394:

```java
double total_bat_kwh = 11.8 * 1000;  // Battery capacity in Wh (11.8 kWh)
```

Change `11.8` to your actual battery capacity in kWh.

### Electricity Rates

Configure your local electricity rates in `Monitor.java` (`createDataset()` method):

**Export Rate** (line 317):
```java
dailyExport * 0.067      // Currently $0.067 per kWh
```

**Import Rate** (line 348):
```java
dailyGrid * 0.19030      // Currently $0.19030 per kWh
```

Find current rates from your utility provider and update these values.

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

### Using the Interface

1. **View Real-Time Data**: Top-right panel updates automatically
2. **Analyze Daily Usage**: Check bottom-left for hourly breakdown
3. **Review Costs**: See daily and monthly cost analysis
4. **Zoom Charts**: Click and drag on chart to zoom into time periods
5. **Refresh Data**: Click "Refresh" button to force data update
6. **Export Data**: Use standard copy/paste from tables

## Data Flow

```
Database Entry → Parser → Calculations → Display Updates
     ↓              ↓            ↓            ↓
[Time Series] [Power/Voltage] [Costs/ROI] [Charts & Tables]
```

### Data Processing Steps

1. **Read**: Query MySQL `solarusage` table at 10-second intervals
2. **Parse**: Extract solar power, grid power, battery power, battery capacity
3. **Calculate**:
   - Daily totals (kWh per day)
   - Revenue from grid export
   - Costs from grid import
   - Battery discharge time remaining
4. **Display**: Update GUI charts, tables, and statistics

## Cost Calculations

The application uses utility rates to calculate financial impact:

### Export Revenue
```
Daily Export Revenue = (Solar kWh Exported) × $0.067/kWh
```

### Grid Import Cost
```
Daily Import Cost = (Grid kWh Imported) × $0.19030/kWh
```

### Net Daily Benefit
```
Net Benefit = Export Revenue - Import Cost
```

### Monthly/Annual Projections
Aggregated from daily calculations with trend analysis.

## Performance Considerations

### Database Queries
- 10-second polling interval balances real-time updates with database load
- Indexed datetime column enables fast historical queries
- Consider archiving old data (>6 months) for better query performance

### GUI Rendering
- Charts render on separate thread to prevent UI freezing
- Large datasets (>1 year history) may impact responsiveness
- Consider limiting chart display to 90-day rolling window

### Memory Usage
- Typical memory footprint: 512MB - 1GB
- Increase heap size for long-term operation: `-Xmx2048m`

### Optimization Tips
- Archive data older than 1 year to separate table
- Use database maintenance: `OPTIMIZE TABLE solarusage;`
- Monitor MySQL process for slow queries: `SHOW PROCESSLIST;`

## Customization

### Changing Time Interval

To modify the data refresh interval (default 10 seconds), find the timer in `Monitor.java`:

```java
Timer timer = new Timer(10000, ...); // 10000 milliseconds = 10 seconds
```

Change `10000` to desired milliseconds.

### Adding More Data Sources

The application architecture supports adding additional sensors:

1. Add columns to `solarusage` table
2. Update `SolarData.java` to parse new fields
3. Modify `Monitor.java` display panels to show new data

### Custom Chart Colors

Edit `createDataset()` and chart rendering code to modify visualization colors.

## Troubleshooting

### Database Connection Issues

**Problem**: "SQLException: No suitable driver found"
- **Solution**: Verify `mysql-connector-java-8.0.30.jar` is in classpath
- Check Maven dependencies: `mvn dependency:tree`
- Rebuild with `mvn clean package`

**Problem**: "Access denied for user 'solarpv'@'localhost'"
- **Solution**: Verify database credentials in `Monitor.java` line 566
- Check MySQL user exists: `mysql -u solarpv -p`
- Ensure password matches: Check `GRANT` statements with `SHOW GRANTS FOR 'solarpv'@'localhost';`
- Test connection: `mysql -h localhost -u solarpv -p solarpv`

**Problem**: "Unknown database 'solarpv'"
- **Solution**: Create database: `CREATE DATABASE solarpv;`
- Verify with: `SHOW DATABASES;`

### Chart Not Displaying

**Problem**: Chart panel is blank
- **Solution**: 
  - Verify data exists in table: `SELECT COUNT(*) FROM solarusage;`
  - Check date format is correct (YYYY-MM-DD HH:MM:SS)
  - Ensure data is within last 30 days
  - Click "Refresh" button manually
  - Check console for exceptions

**Problem**: Chart shows no data points
- **Solution**:
  - Insert test data: 
    ```sql
    INSERT INTO solarusage VALUES (NULL, NOW(), 5000, 2000, 1000, 75);
    ```
  - Verify query results: `SELECT * FROM solarusage ORDER BY dateTime DESC LIMIT 1;`

### Application Won't Start

**Problem**: "Exception in thread 'main'"
- **Solution**: 
  - Check Java version: `java -version` (must be 17+)
  - Verify JAR file exists: `ls -la target/SolarEnegeryMonitor-1.0.1-SNAPSHOT-jar-with-dependencies.jar`
  - Try increasing memory: `java -Xmx1024m -jar target/...jar`

**Problem**: "Main class not found"
- **Solution**:
  - Rebuild with Maven: `mvn clean package`
  - Verify main class in pom.xml has correct entry

### GUI Rendering Issues

**Problem**: Window displays but is blank/frozen
- **Solution**:
  - Wait 30 seconds for initial data load
  - Check MySQL is running: `mysql -u root -p -e "SELECT 1;"`
  - Verify database has data: `SELECT COUNT(*) FROM solarusage;`
  - Review console for connection errors

**Problem**: Text is too small or too large
- **Solution**: 
  - Adjust Java font settings (system-dependent)
  - Increase screen resolution
  - Modify font sizes in `Monitor.java` GUI configuration

### Performance Issues

**Problem**: Application is slow/laggy
- **Solution**:
  - Increase Java heap: `java -Xmx2048m -jar ...jar`
  - Check MySQL: `SHOW PROCESSLIST;` for slow queries
  - Reduce chart date range (limit to 90 days)
  - Archive old data to separate table

**Problem**: High CPU usage
- **Solution**:
  - Reduce refresh rate from 10 to 30 seconds
  - Check for database locks: `SHOW OPEN TABLES WHERE In_use > 0;`
  - Monitor MySQL: `SHOW STATUS;`

### Data Issues

**Problem**: Missing or incorrect data in display
- **Solution**:
  - Verify data exists: `SELECT * FROM solarusage WHERE dateTime > NOW() - INTERVAL 1 DAY;`
  - Check for NULL values that may cause parsing errors
  - Validate column names match code expectations
  - Test SQL directly: `mysql -u solarpv -p solarpv -e "SELECT * FROM solarusage LIMIT 5;"`

**Problem**: Cost calculations are incorrect
- **Solution**:
  - Verify electricity rates in code (lines 317, 348)
  - Check battery capacity setting (line 394)
  - Ensure power values are in Watts, not kilowatts
  - Verify grid direction (positive = export, negative = import)

## Known Limitations

- Single database instance supported (no clustering)
- No multi-user concurrent access controls
- Chart rendering performance degrades with >1 year of data
- No built-in data export to CSV/Excel (use database directly)
- Fixed electricity rates (requires code modification to change)
- No alert/notification system for threshold events
- Limited to system timezone (no multi-timezone support)

## Future Enhancements

- **Advanced Analytics**: Machine learning for consumption prediction
- **Mobile App**: Companion app for mobile device monitoring
- **Cloud Sync**: Backup data to cloud storage
- **Real-time Alerts**: Email/SMS notifications for events
- **Multi-Site Support**: Monitor multiple solar installations
- **Export Functionality**: CSV, PDF reports generation
- **Energy Optimization**: Recommendations for usage optimization
- **Battery Health**: Degradation tracking and predictions
- **API Interface**: RESTful API for third-party integration
- **Dark Mode**: Dark theme option for GUI
- **Configuration UI**: GUI-based settings instead of code editing

## Development Tips

### Debugging Database Queries

Enable MySQL query logging:
```sql
SET GLOBAL log_queries_not_using_indexes = 'ON';
SET GLOBAL long_query_time = 0.5;
```

### Testing with Sample Data

Insert test data:
```sql
INSERT INTO solarusage VALUES 
  (NULL, DATE_SUB(NOW(), INTERVAL 1 HOUR), 4500, 1000, 500, 85),
  (NULL, NOW(), 5000, 2000, 1000, 75);
```

### Development Build

For faster builds during development:
```bash
mvn compile exec:java -Dexec.mainClass="au.net.dias.solar.Monitor"
```

### Debugging Console Output

The application prints debug information to console. Capture it:
```bash
java -jar target/...jar > solar_monitor.log 2>&1 &
tail -f solar_monitor.log
```

## Integration with Solar Inverters

This application reads data from a MySQL database populated by your solar inverter system. Setup steps:

1. **Inverter Configuration**: Configure your solar inverter to log data to MySQL
2. **Data Mapping**: Ensure inverter logs match column names in schema
3. **Polling Interval**: Recommend 10-30 second intervals
4. **Timezone**: Ensure inverter system clock is accurate

Common inverter integration:
- **SolarEdge**: API → Custom Python script → MySQL
- **Fronius**: ModBus/HTTP API → Data logger → MySQL
- **Victron**: VRM Cloud API → Integration script → MySQL

## License

This project is provided as-is without any specific license terms.

## Credits

- **Developer**: Adilson Dias
- **Framework**: Java Swing, JFreeChart
- **Database**: MySQL
- **Build Tool**: Apache Maven

## Contact

**Tiny Memories Laser (TML)**  
API-Led Pty Ltd  
[GitHub](https://github.com/adilsondias-engineer)

---

*This Solar Energy Monitor demonstrates real-time data visualization, financial analytics, and energy management for residential solar PV systems. A practical tool for optimizing renewable energy usage and tracking solar investment ROI.*