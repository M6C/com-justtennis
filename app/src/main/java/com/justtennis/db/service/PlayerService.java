package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBPlayerDataSource;
import com.justtennis.domain.Player;
import com.justtennis.manager.TypeManager;
import com.justtennis.R;

public class PlayerService extends PersonService<Player> {

	public static final long ID_EMPTY_PLAYER = -2l;
	private static final long ID_UNKNOWN_PLAYER = -1l;

	private static Random rnd = new Random(1);

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

	public static @DrawableRes int getUnknownPlayerRandomRes() {
		switch (rnd.nextInt(3)) {
			default:
			case 0:
				return R.drawable.ic_tennis_player_03;
			case 1:
				return R.drawable.ic_tennis_player_04;
			case 2:
				return R.drawable.ic_tennis_player_05;
		}
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
