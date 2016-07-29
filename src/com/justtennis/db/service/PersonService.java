package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.RechercheService.TYPE;
import com.justtennis.db.sqlite.datasource.DBPersonDataSource;
import com.justtennis.domain.Person;
import com.justtennis.domain.RechercheResult;

public class PersonService<P extends Person> extends GenericService<P> {

	private static final String TAG = PersonService.class.getCanonicalName();
	
	public PersonService(Context context, DBPersonDataSource<P> dbDataSource, INotifierMessage notificationMessage) {
		super(context, dbDataSource, notificationMessage);
	}

	public Collection<RechercheResult> find(String text) {
		Date dateStart = new Date();
		List<RechercheResult> ret = new ArrayList<RechercheResult>();
		try {
			dbDataSource.open();
			List<P> list = ((DBPersonDataSource<P>)dbDataSource).getLikeByName(text);
			for(P p : list) {
				ret.add(new RechercheResult(TYPE.TOURNAMENT, p.getId(), p.getFullName()));
			}
			logMe("find(data:" + text + ", tournament.size:"+list.size()+")", dateStart);
		}
		finally {
			dbDataSource.close();
		}
		return ret;
	}

	protected String getTag() {
		return TAG;
	}

	protected void logMe(String msg, Date dateStart) {
		logMe("DB Execution time:" + (new Date().getTime() - dateStart.getTime()) + "millisecond - " + msg);
    }
}