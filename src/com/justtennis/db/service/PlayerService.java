package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.sqlite.datasource.DBPlayerDataSource;
import com.justtennis.domain.Player;
import com.justtennis.manager.TypeManager;

public class PlayerService extends GenericService<Player> {

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
