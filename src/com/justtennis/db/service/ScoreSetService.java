package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBScoreSetDataSource;
import com.justtennis.domain.ScoreSet;

public class ScoreSetService extends GenericService<ScoreSet> {

	public ScoreSetService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBScoreSetDataSource(context, notificationMessage), notificationMessage);
	}

	public List<ScoreSet> getByIdInvite(long idInvite) {
    	try {
    		dbDataSource.open();
    		return ((DBScoreSetDataSource)dbDataSource).getByIdInvite(idInvite);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public String[][] getTableByIdInvite(long idInvite) {
		String[][] ret = null;
    	try {
    		dbDataSource.open();
    		int len = 0;
    		List<ScoreSet> list = ((DBScoreSetDataSource)dbDataSource).getByIdInvite(idInvite);
    		if (list != null) {
	    		len = list.size();
	    		for(ScoreSet score : list) {
	    			if (score.getOrder() > len) {
	    				len = score.getOrder();
	    			}
	    		}
	    		ret = new String[len][2];
	    		for(ScoreSet score : list) {
	    			ret[score.getOrder() - 1] = new String[]{Integer.toString(score.getValue1()), Integer.toString(score.getValue2())};
	    		}
    		}
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}
	
	public void deleteByIdInvite(long idInvite) {
		try {
			dbDataSource.open();
			((DBScoreSetDataSource)dbDataSource).deleteByIdInvite(idInvite);
		}
		finally {
			dbDataSource.close();
		}
	}
}
