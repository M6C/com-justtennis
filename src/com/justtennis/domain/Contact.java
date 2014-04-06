package com.justtennis.domain;

import java.util.List;

import android.graphics.Bitmap;

public class Contact extends Person {

	private static final long serialVersionUID = 1L;

	private List<Phone> listPhone;
	private Bitmap photo;
	private String lookup;
	private boolean hasPhone;

	public Contact() {
		super();
	}

	public Contact(Long id) {
		super(id);
	}

	public List<Phone> getListPhone() {
		return listPhone;
	}

	public void setListPhone(List<Phone> listPhone) {
		this.listPhone = listPhone;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public boolean hasPhone() {
		return hasPhone;
	}

	public void setHasPhone(boolean hasPhone) {
		this.hasPhone = hasPhone;
	}
}