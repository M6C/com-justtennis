package com.justtennis.domain;



public class Club extends GenericDBPojoNamed {
	
	private static final long serialVersionUID = 1L;

	private Long idAddress;

	public Long getIdAddress() {
		return idAddress;
	}
	public void setIdAddress(Long idAddress) {
		this.idAddress = idAddress;
	}
}