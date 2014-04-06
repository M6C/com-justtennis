package com.justtennis.domain;


public class Message extends GenericDBPojo<Long> {
	
	private static final long serialVersionUID = -853644798383224239L;
	private String message;
	private Long idPlayer;

	public Message() {
		super();
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