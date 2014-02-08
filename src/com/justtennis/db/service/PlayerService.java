package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.db.sqlite.datasource.DBPlayerDataSource;
import com.justtennis.domain.Player;

public class PlayerService extends GenericService<Player> {

	private static final long ID_UNKNOWN_PLAYER = -1l;

	public PlayerService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBPlayerDataSource(context, notificationMessage), notificationMessage);
	}
	
	public Player getUnknownPlayer() {
		Player ret = new Player();
		ret.setId(ID_UNKNOWN_PLAYER);
		ret.setFirstName(context.getString(R.string.listplayer_unknown_player_firstname));
		ret.setLastName(context.getString(R.string.listplayer_unknown_player_lastname));
		return ret;
	}

	public boolean isUnknownPlayer(Player player) {
		return player.getId()!=null && ID_UNKNOWN_PLAYER==player.getId();
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
