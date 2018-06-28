package com.justtennis.listener.itemclick;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.justtennis.R;
import com.justtennis.adapter.viewholder.CommonListViewHolder;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.fragment.PlayerFragment;

public class OnItemClickListPlayer implements OnItemClickListener {
	private Activity context;

	public OnItemClickListPlayer(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long idDumb) {
		long id = (long)((CommonListViewHolder)view.getTag()).data.getId();
		PlayerFragment fragment = PlayerFragment.build(id);
		FragmentTool.replaceFragment((FragmentActivity) context, fragment, R.id.item_detail_container);
    }
}