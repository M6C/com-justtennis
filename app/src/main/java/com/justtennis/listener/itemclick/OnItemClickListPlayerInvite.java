package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.InviteActivity;
import com.justtennis.ui.common.CommonEnum;

public class OnItemClickListPlayerInvite implements OnItemClickListener {
	private Activity context;
	
	public OnItemClickListPlayerInvite(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_SIMPLE);
		intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, ((Long)view.getTag()));
		context.startActivity(intent);
    }
}