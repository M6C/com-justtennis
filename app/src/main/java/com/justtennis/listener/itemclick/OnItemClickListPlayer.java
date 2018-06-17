package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.PlayerActivity;
import com.justtennis.adapter.viewholder.CommonListViewHolder;

public class OnItemClickListPlayer implements OnItemClickListener {
	private Activity context;

	public OnItemClickListPlayer(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, PlayerActivity.class);
		intent.putExtra(PlayerActivity.EXTRA_PLAYER_ID, (long)((CommonListViewHolder)view.getTag()).data.getId());
		context.startActivity(intent);
    }
}