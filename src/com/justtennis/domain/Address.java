package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojoNamedSubId;



public class Address extends GenericDBPojoNamedSubId {
	
	private static final long serialVersionUID = 1L;

	private String line1;
	private String postalCode;
	private String city;
	private String gps;

	public Address() {
		super();
	}

	public Address(Long id) {
		super(id);
	}

	public Address(Long id, String name) {
		super(id, name);
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