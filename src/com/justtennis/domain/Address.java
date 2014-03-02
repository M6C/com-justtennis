package com.justtennis.domain;

import com.justtennis.domain.interfaces.IPojoNamed;


public class Address extends GenericDBPojo<Long> implements IPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private String name;
	private String line1;
	private String postalCode;
	private String city;
	private String gps;

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getGps() {
		return gps;
	}
	public void setGps(String gps) {
		this.gps = gps;
	}
}