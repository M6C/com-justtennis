package com.justtennis.drawer.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.justtennis.domain.RechercheResult;
import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;
import com.justtennis.drawer.business.INavigationDrawerRechercheBusiness;
import com.justtennis.R;

public class NavigationDrawerRechercheNotifer implements INavigationDrawerNotifer {

	private EditText edtRecherche;
	private ListView listRecherche;
	private BaseAdapter adpRecherche;
	private List<RechercheResult> list = new ArrayList<RechercheResult>();
	private Handler handler = new Handler();
	private INavigationDrawerRechercheBusiness business;
	private INavigationDrawerRechercheNotifer navigationDrawerRechercheNotifer;

	public NavigationDrawerRechercheNotifer(INavigationDrawerRechercheNotifer navigationDrawerRechercheNotifer) {
		this.navigationDrawerRechercheNotifer = navigationDrawerRechercheNotifer;
		this.business = navigationDrawerRechercheNotifer.getBusiness();
	}

	@Override
	public void onCreateView(View view) {
		edtRecherche = (EditText) view.findViewById(R.id.edt_recherche);
		listRecherche = (ListView) view.findViewById(R.id.lst_recherche);
		adpRecherche = this.navigationDrawerRechercheNotifer.getAdapter(view.getContext().getApplicationContext(), list);
		listRecherche.setAdapter(adpRecherche);

		edtRecherche.setText(business.getFindText());
		edtRecherche.addTextChangedListener(
			    new TextWatcher() {
			        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
			        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			        private Timer timer=new Timer();
			        private final long DELAY = 1000; // milliseconds

			        @Override
			        public void afterTextChanged(Editable s) {
			        	final String text = s.toString();
			            timer.cancel();
			            timer = new Timer();
			            timer.schedule(
			                new TimerTask() {
			                    @Override
			                    public void run() {
			                    	business.setFindText(text);
			                    	list.clear();
			                    	if (text != null && !text.trim().isEmpty()) {
										list.addAll(business.find(text));		                    	}
			                    	handler.post(new Runnable() {
										
										@Override
										public void run() {
					                    	adpRecherche.notifyDataSetChanged();
										}
									});
			                    }
			                }, 
			                DELAY
			            );
			        }
			    }
			);
		}

	@Override
	public void onUpdateView(View view) {
	}

	public static interface INavigationDrawerRechercheNotifer {
		public BaseAdapter getAdapter(Context context, List<RechercheResult> list);
		public INavigationDrawerRechercheBusiness getBusiness();
	}
}
