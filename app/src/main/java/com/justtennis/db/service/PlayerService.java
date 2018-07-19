package com.justtennis.db.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.sqlite.datasource.DBPlayerDataSource;
import com.justtennis.domain.Player;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.ResourceTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerService extends PersonService<Player> {

	public static final long ID_EMPTY_PLAYER = -2l;
	private static final long ID_UNKNOWN_PLAYER = -1l;

	public PlayerService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBPlayerDataSource(context, notificationMessage), notificationMessage);
	}

	public Player getUnknownPlayer() {
		Player ret = new Player();
		ret.setId(ID_UNKNOWN_PLAYER);
		ret.setFirstName(context.getString(R.string.listplayer_unknown_player_firstname));
		ret.setLastName(context.getString(R.string.listplayer_unknown_player_lastname));
		ret.setType(TypeManager.getInstance().getType());
		return ret;
	}
	
	public Player getEmptyPlayer() {
		Player ret = new Player();
		ret.setId(ID_EMPTY_PLAYER);
		ret.setType(TypeManager.getInstance().getType());
		return ret;
	}

	public static Drawable getUnknownPlayerRandomRes(Activity activity) {
		int id = ResourceTool.getIdDrawableInStyleAttr(activity, R.attr.customListPlayerDrawable);
		if (id <= 0) {
			id = R.drawable.player_unknow;
		}
		return activity.getResources().getDrawable(id);
	}

	public static boolean isUnknownPlayer(Player player) {
		return player.getId()!=null && ID_UNKNOWN_PLAYER==player.getId();
	}
	
	public static boolean isEmptyPlayer(Player player) {
		return player.getId()!=null && ID_EMPTY_PLAYER==player.getId();
	}

	@SuppressLint("UseSparseArrays")
	public HashMap<Long, List<Player>> getGroupByIdRanking() {
		HashMap<Long, List<Player>> ret = new HashMap<Long, List<Player>>();
		List<Player> listPlayer = null;
		try {
			dbDataSource.open();
			listPlayer = ((DBPlayerDataSource)dbDataSource).getAll();
		}
		finally {
			dbDataSource.close();
		}
		if (listPlayer != null) {
			for(Player invite : listPlayer) {
				Long key = invite.getIdRanking();
				List<Player> list = ret.get(key);
				if (list == null) {
					list = new ArrayList<Player>();
					ret.put(key, list);
				}
				list.add(invite);
			}
		}
		return ret;
	}
	
	@Override
	public Player find(long id) {
		if (id==ID_UNKNOWN_PLAYER) {
			return getUnknownPlayer();
		} else {
			return super.find(id);
		}
	}
}
