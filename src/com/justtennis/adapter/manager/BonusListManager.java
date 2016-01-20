package com.justtennis.adapter.manager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.db.service.BonusService;
import com.justtennis.domain.Bonus;
import com.justtennis.domain.Invite;
import com.justtennis.notifier.NotifierMessageLogger;

public class BonusListManager {
	
	private static BonusListManager instance;

	private BonusService service;
	private List<Bonus> list;
	private String[] listTxt;

	private BonusListManager(Context context, NotifierMessageLogger notifier) {
		service = new BonusService(context, notifier);
		initializeData();
	}

	public static BonusListManager getInstance(Context context, NotifierMessageLogger notifier) {
		if (instance == null) {
			instance = new BonusListManager(context, notifier);
		}
		return instance;
	}

	public void manage(Activity context, final Invite invite) {
		IBonusListListener listener = new IBonusListListener() {
			@Override
			public void onSelected(Bonus bonus) {
				invite.setBonusPoint(bonus.getPoint());
			}
		};
		manage(context, listener, invite.getBonusPoint());
	}

	public void manage(final Activity context, final IBonusListListener listener, int point) {
		final TextView tvBonus = (TextView)context.findViewById(R.id.tv_bonus_point);
		final Spinner spBonus = (Spinner)context.findViewById(R.id.sp_bonus_point);

//		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listTxt);
//		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_bonus, listTxt);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item_bonus);
		spBonus.setAdapter(dataAdapter);

		spBonus.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)spBonus.findViewById(android.R.id.text1);
				Bonus ranking = list.get(position);
				listener.onSelected(ranking);
				if (position == 0) {
					tv.setText(context.getString(R.string.txt_bonus_point));
					tv.setTextColor(context.getResources().getColor(R.color.spinner_color_hint));
					tvBonus.setVisibility(View.GONE);
				} else {
					tv.setTextColor(context.getResources().getColor(android.R.color.black));
					tvBonus.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (point > 0) {
			initialize(spBonus, point);
		}
	}
	
	private void initialize(Spinner spRanking, int point) {
		int position = getPosition(point);
		if (position < list.size()) {
			spRanking.setSelection(position, true);
		}
	}

	private int getPosition(int point) {
		int position = 0;
		for(Bonus r : list) {
			if (r.getPoint() == point) {
				break;
			} else {
				position++;
			}
		}
		return position;
	}

	/**
	 * BUSINESS
	 */
	private void initializeData() {
		list = service.getList();

		int i=0;
		listTxt = new String[list.size()];
		for(Bonus ranking : list) {
			listTxt[i++] = String.valueOf(ranking.getPoint());
		}
	}

	/**
	 * INNER INTERFACE
	 */
	public interface IBonusListListener {
		void onSelected(Bonus bonus);
	}
}