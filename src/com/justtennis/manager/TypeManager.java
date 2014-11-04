package com.justtennis.manager;

import android.content.Context;
import android.view.View;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.service.SaisonService;
import com.justtennis.domain.Saison;

public class TypeManager {

	public enum TYPE {
		TRAINING,
		COMPETITION
	}

	private static TypeManager instance;
	private TYPE type = TYPE.TRAINING;
	private Saison saison;

	private TypeManager() {
	}

	public static TypeManager getInstance() {
		if (instance == null) {
			instance = new TypeManager();
		}
		return instance;
	}

	public void initialize(Context context, INotifierMessage notificationMessage) {
		SaisonService saisonService = new SaisonService(context, notificationMessage);
		saison = saisonService.getSaisonActiveOrFirst();
		if (saison == null) {
			saison = SaisonService.getEmpty();
		} else {
//			Saison saisonOld = SaisonService.build(2013);
//			System.out.println("Begin:" + ToolDatetime.toDatetimeDefault(saisonOld.getBegin()));
//			System.out.println("End:" + ToolDatetime.toDatetimeDefault(saisonOld.getEnd()));
//			saisonOld.setId(saison.getId());
//			saisonService.createOrUpdate(saisonOld);
		}
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public Saison getSaison() {
		return saison;
	}

	public void setSaison(Saison saison) {
		this.saison = saison;
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