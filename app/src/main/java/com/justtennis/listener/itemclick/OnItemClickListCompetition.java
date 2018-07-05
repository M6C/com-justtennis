package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.justtennis.activity.InviteActivity;
import com.justtennis.ui.common.CommonEnum;

import java.io.Serializable;

public class OnItemClickListCompetition implements OnChildClickListener {
	private Activity context;
	private int requestCode;
	
	public OnItemClickListCompetition(Activity context, int requestCode) {
		this.context = context;
		this.requestCode = requestCode;
	}

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
    	Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_INVITE, (Serializable)view.getTag());
		intent.putExtra(InviteActivity.EXTRA_MODE, CommonEnum.INVITE_MODE.INVITE_DETAIL);
		context.startActivityForResult(intent, requestCode);
		return false;
    }
}