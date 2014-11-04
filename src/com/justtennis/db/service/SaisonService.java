package com.justtennis.db.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.ToolDatetime;
import com.justtennis.db.sqlite.datasource.DBSaisonDataSource;
import com.justtennis.domain.Saison;

public class SaisonService extends GenericNamedService<Saison> {

	public static final long ID_EMPTY = -1l;

	public SaisonService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBSaisonDataSource(context, notificationMessage), notificationMessage);
	}
	
	public static Saison getEmpty() {
		return new Saison(ID_EMPTY);
	}

	public static boolean isEmpty(Saison saison) {
		return saison.getId()!=null && ID_EMPTY==saison.getId();
	}

	public static Saison build(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		if (month < 10) {
			year--;
		}
		return build(year, month);
	}

	public static Saison build(int year) {
		return build(year, 10);
	}

	public static Saison build(int year, int month) {
		Saison ret = new Saison();

		Calendar cal = Calendar.getInstance();

		if (month < 10) {
			year--;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 9);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		ret.setBegin(new Date(cal.getTimeInMillis()));

		cal.set(Calendar.DAY_OF_MONTH, 30);
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.YEAR, year + 1);

		ret.setEnd(new Date(cal.getTimeInMillis()));

		ret.setName(year + "/" + (year + 1));

		ret.setActive(true);

		logMe("saison build date begin:" + ToolDatetime.toDatetimeDefault(ret.getBegin()));
		logMe("saison build date end:" + ToolDatetime.toDatetimeDefault(ret.getEnd()));

		return ret;
	}

	public boolean exist(int year) {
		boolean ret = false;

		List<Saison> saisons = getList();
		if (saisons != null && saisons.size() > 0) {
			for(Saison saison : saisons) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(saison.getBegin());
				if (cal.get(Calendar.YEAR) == year) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public Saison create(int year, boolean active) {
		Saison saison = build(year, 10);
		saison.setActive(active);
		createOrUpdate(saison);
		return saison;
	}

	@Override
	public void createOrUpdate(Saison pojo) {
		Long id = pojo.getId();
		super.createOrUpdate(pojo);
		if (id == null && pojo.isActive()) {
	    	desactiveExcept(pojo);
		}
	}

	public void desactiveExcept(Saison pojo) {
		try {
			dbDataSource.open();
			((DBSaisonDataSource)dbDataSource).desactiveExcept(pojo);
		}
		finally {
			dbDataSource.close();
		}
	}

	public Saison getSaisonActiveOrFirst() {
		Saison ret = null;
		List<Saison> saisons = getList();
		if (saisons != null && saisons.size() > 0) {
			for(Saison saison : saisons) {
				if (saison.isActive()) {
					ret = saison;
					break;
				}
			}
			if (ret == null) {
				ret = saisons.get(0);
			}
		}
		return ret;
	}
}
