package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.InviteActivity;
import com.justtennis.domain.Invite;
import com.justtennis.ui.common.CommonEnum;

public class OnItemClickListInvite implements OnItemClickListener {
	private Activity context;
	private int requestCode;
	
	public OnItemClickListInvite(Activity context, int requestCode) {
		this.context = context;
		this.requestCode = requestCode;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_INVITE, (Invite)view.getTag());
		intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
		context.startActivityForResult(intent, requestCode);
    }
}