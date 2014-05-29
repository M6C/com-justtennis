package com.justtennis.manager;

import com.justtennis.R;

import android.view.View;

public class TypeManager {

	public enum TYPE {
		TRAINING,
		COMPETITION
	}

	private static TypeManager instance;
	private TYPE type = TYPE.TRAINING;

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

	public void initializeActivity(View view, boolean main) {
		switch(type) {
			case COMPETITION: {
				view.setBackgroundResource(main ? R.drawable.background_01_orange : R.drawable.background_03_orange);
			}
			break;
			default:
			case TRAINING: {
				view.setBackgroundResource(main ? R.drawable.background_01 : R.drawable.background_02);
			}
			break;
		}
	}

	public int getThemeResource() {
		switch(getType()) {
			case COMPETITION: {
				return R.style.AppTheme_Competition;
			}
	
			default:
			case TRAINING: {
				return R.style.AppTheme;
			}
		}
		
	}
}