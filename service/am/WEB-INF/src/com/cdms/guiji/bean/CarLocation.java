package com.cdms.guiji.bean;

import java.io.Serializable;

public class CarLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String license_plate_number;
	private String positioning_time;
	private String speed;
	private String current_mileage;
	private String location;
	private String car_state;
	
	public String getLicense_plate_number() {
		return license_plate_number;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCar_state() {
		return car_state;
	}
	public void setCar_state(String car_state) {
		this.car_state = car_state;
	}
	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	public String getPositioning_time() {
		return positioning_time;
	}
	public void setPositioning_time(String positioning_time) {
		this.positioning_time = positioning_time;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getCurrent_mileage() {
		return current_mileage;
	}
	public void setCurrent_mileage(String current_mileage) {
		this.current_mileage = current_mileage;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
