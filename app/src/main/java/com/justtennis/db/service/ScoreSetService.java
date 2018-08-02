package com.justtennis.db.service;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.sqlite.datasource.DBScoreSetDataSource;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Invite.SCORE_RESULT;
import com.justtennis.domain.ScoreSet;

import java.util.List;

public class ScoreSetService extends GenericService<ScoreSet> {

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
    		int len;
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
		else if (invite.getListScoreSet()!=null && !invite.getListScoreSet().isEmpty()) {
			ret = getTextScore(invite);
		}
		return ret;
	}

	public static SCORE_RESULT getInviteScoreResult(List<ScoreSet> listScoreSet) {
		Invite.SCORE_RESULT ret = Invite.SCORE_RESULT.UNFINISHED;
		int size = listScoreSet.size();
		if (size > 0) {
			ScoreSet scoreLast = listScoreSet.get(size-1);

			int iCol0 = (scoreLast.getValue1() == null  ? 0 : scoreLast.getValue1().intValue());
			int iCol1 = (scoreLast.getValue2() == null  ? 0 : scoreLast.getValue2().intValue());
			if (iCol0 == -1) {
				ret = Invite.SCORE_RESULT.WO_VICTORY;
			} else if (iCol1 == -1) {
				ret = Invite.SCORE_RESULT.WO_DEFEAT;
			} else if (iCol0 > iCol1) {
				ret = Invite.SCORE_RESULT.VICTORY;
			} else if (iCol0 < iCol1) {
				ret = Invite.SCORE_RESULT.DEFEAT;
			}
		}
		return ret;
	}

	private String getTextScore(Invite invite) {
		StringBuilder ret = null;
		for(ScoreSet score : invite.getListScoreSet()) {
            if (score.getValue1() > 0 || score.getValue2() > 0) {
                String score1 = (score.getValue1() > score.getValue2() ? "<b>" + score.getValue1() + "</b>": score.getValue1().toString());
                String score2 = (score.getValue2() > score.getValue1() ? "<b>" + score.getValue2() + "</b>": score.getValue2().toString());
                if (ret == null) {
                    ret = new StringBuilder(score1 + "-" + score2);
                } else {
                    ret.append(" / ").append(score1).append("-").append(score2);
                }
            }
        }
		return (ret == null) ? null : ret.toString();
	}
}
