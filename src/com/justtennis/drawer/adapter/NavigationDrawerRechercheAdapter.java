package com.justtennis.drawer.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.domain.RechercheResult;
import com.justtennis.R;

public class NavigationDrawerRechercheAdapter extends BaseAdapter {

	private List<RechercheResult> list;
	private Context context;
	private OnClickListener onItemClickListener;

	public NavigationDrawerRechercheAdapter(Context context, List<RechercheResult> list, OnClickListener onItemClickListener) {
		this.context = context;
		this.list = list;
		this.onItemClickListener = onItemClickListener;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.fragment_navigation_player_drawer_element_recherche_view, null);
			if (onItemClickListener != null) {
				convertView.setOnClickListener(onItemClickListener);
			}
		}
		ImageView ivRechercheType = (ImageView) convertView.findViewById(R.id.imv_recherche_type);
		TextView tvRechercheText = (TextView) convertView.findViewById(R.id.edt_recherche_text);

		RechercheResult item = (RechercheResult) getItem(position);
		switch (item.getType()) {
			case TOURNAMENT:
				ivRechercheType.setImageResource(R.drawable.ic_location_tournament);
				break;

			case CLUB:
				ivRechercheType.setImageResource(R.drawable.ic_location_club);
				break;

			default:
			case ADDRESS:
				ivRechercheType.setImageResource(R.drawable.ic_location_address_1);
				break;
		}
		tvRechercheText.setText(item.getData());
		convertView.setTag(item);
		return convertView;
	}
}