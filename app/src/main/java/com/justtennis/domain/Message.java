package com.justtennis.domain;

import com.cameleon.common.android.model.GenericDBPojo;


public class Message extends GenericDBPojo<Long> {
	
	private static final long serialVersionUID = -853644798383224239L;
	private String message;
	private Long idPlayer;

	public Message() {
		super();
	}

	public Message(String message) {
		super();
		this.message = message;
	}
	
	public Message(Long id) {
		super(id);
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getIdPlayer() {
		return idPlayer;
	}
	public void setIdPlayer(Long idPlayer) {
		this.idPlayer = idPlayer;
	}
}