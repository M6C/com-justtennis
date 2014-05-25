package com.justtennis.manager;

public class TypeManager {

	public enum TYPE {
		ENTRAINEMENT,
		MATCH
	}

	private static TypeManager instance;
	private TYPE type = TYPE.ENTRAINEMENT;

	private TypeManager() {
	}

	public static TypeManager getInstance() {
		if (instance == null) {
			instance = new TypeManager();
		}
		return instance;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}
}