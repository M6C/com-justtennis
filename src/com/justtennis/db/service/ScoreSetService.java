package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.sqlite.datasource.DBScoreSetDataSource;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.ScoreSet;

public class ScoreSetService extends GenericService<ScoreSet> {

	private Context context;

	public ScoreSetService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBScoreSetDataSource(context, notificationMessage), notificationMessage);
		this.context = context;
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

	public String buildTextScore(Invite invite) {
		String ret = null;
		if (SCORE_RESULT.WO_VICTORY.equals(invite.getScoreResult())) {
			ret = context.getString(R.string.txt_wo_vitory);
		}
		else if (SCORE_RESULT.WO_DEFEAT.equals(invite.getScoreResult())) {
			ret = context.getString(R.string.txt_wo_defeat);
		}
		else if (invite.getListScoreSet()!=null && invite.getListScoreSet().size() > 0) {
			for(ScoreSet score : invite.getListScoreSet()) {
				if (score.getValue1() > 0 || score.getValue2() > 0) {
					String score1 = (score.getValue1() > score.getValue2() ? "<b>" + score.getValue1() + "</b>": score.getValue1().toString());
					String score2 = (score.getValue2() > score.getValue1() ? "<b>" + score.getValue2() + "</b>": score.getValue2().toString());
					if (ret == null) {
						ret = score1 + "-" + score2;
					} else {
						ret += " / " + score1 + "-" + score2;
					}
				}
			}
		}
		return ret;
	}
}
