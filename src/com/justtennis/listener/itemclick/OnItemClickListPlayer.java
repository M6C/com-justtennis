package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.PlayerActivity;

public class OnItemClickListPlayer implements OnItemClickListener {
	private Activity context;
	
	public OnItemClickListPlayer(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		Player player = ((ListPlayerAdapter.ViewHolder)view.getTag()).player;
//		Intent intent = new Intent(context, PlayerActivity.class);
//		intent.putExtra(PlayerActivity.EXTRA_PLAYER_ID, player.getId());
//		context.startActivity(intent);
		Intent intent = new Intent(context, PlayerActivity.class);
		intent.putExtra(PlayerActivity.EXTRA_PLAYER_ID, ((Long)view.getTag()));
		context.startActivity(intent);
		
//		context.finish();
    }
}