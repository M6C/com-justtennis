package com.justtennis.listener.itemclick;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.InviteActivity;

public class OnItemClickListInvite implements OnItemClickListener {
	private Context context;
	
	public OnItemClickListInvite(Context context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_INVITE, (Serializable)view.getTag());
		intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_MODIFY);
		context.startActivity(intent);
    }
}