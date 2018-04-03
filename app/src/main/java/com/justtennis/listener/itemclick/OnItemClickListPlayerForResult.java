package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.activity.ListPlayerActivity;

public class OnItemClickListPlayerForResult implements OnItemClickListener {
	private Activity context;
	
	public OnItemClickListPlayerForResult(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		intent.putExtra(ListPlayerActivity.EXTRA_PLAYER_ID, ((Long)view.getTag()));
		context.setResult(Activity.RESULT_OK, intent);
		context.finish();
    }
}