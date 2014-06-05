package com.justtennis.listener.itemclick;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.justtennis.activity.InviteActivity;

public class OnItemClickListCompetition implements OnChildClickListener {
	private Context context;
	
	public OnItemClickListCompetition(Context context) {
		this.context = context;
	}

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
    	Intent intent = new Intent(context, InviteActivity.class);
		intent.putExtra(InviteActivity.EXTRA_INVITE, (Serializable)view.getTag());
		intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_MODIFY);
		context.startActivity(intent);
		return false;
    }
}