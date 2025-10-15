package au.net.dias.solar;

import java.util.Date;

public class SolarData {

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSolarPower() {
		return solarPower;
	}
	public void setSolarPower(int solarPower) {
		this.solarPower = solarPower;
	}
	public int getGridPower() {
		return gridPower;
	}
	public void setGridPower(int gridPower) {
		this.gridPower = gridPower;
	}
	public int getBatteryPower() {
		return batteryPower;
	}
	public void setBatteryPower(int batteryPower) {
		this.batteryPower = batteryPower;
	}
	public int getBatteryCapacity() {
		return batteryCapacity;
	}
	public void setBatteryCapacity(int batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}
	private Date date;
	private int id;
	private int solarPower;
	private int gridPower;
	private int batteryPower;
	private int batteryCapacity;
	
}
