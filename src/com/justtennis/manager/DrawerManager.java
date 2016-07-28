package com.justtennis.manager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerData;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;
import com.justtennis.domain.Saison;
import com.justtennis.fragment.NavigationDrawerFragment;
import com.justtennis.manager.TypeManager.TYPE;
import com.justtennis.manager.business.DrawerBusiness;
import com.justtennis.R;

public class DrawerManager {

	private static final String TAG = DrawerManager.class.getSimpleName();

	private Context context;
	private Activity activity;
	private INotifierMessage notificationMessage;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private NavigationDrawerSaisonNotifer notiferSaison;
	private TypeManager typeManager;
	private ViewGroup container;
	private IDrawerLayoutTypeNotifier drawerLayoutTypeNotifier = null;
	private IDrawerLayoutSaisonNotifier drawerLayoutSaisonNotifier = null;
	private View drawerLayout;
	private DrawerBusiness business;

	public DrawerManager(Activity activity, INotifierMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
		this.context = activity.getApplicationContext();
		this.activity = activity;

		this.business = new DrawerBusiness(context, notificationMessage);
		this.notiferSaison = new NavigationDrawerSaisonNotifer();
		this.typeManager = TypeManager.getInstance(context, notificationMessage);

		business.initializeDataSaison();
	}

	public void setContentView(int layoutResId) {
		activity.setContentView(R.layout.generic_main_drawer);

		container = (ViewGroup) activity.findViewById(R.id.container);

		LayoutInflater inflater = ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View footerView =  inflater.inflate(layoutResId, null, false);

        container.addView(footerView);
		container.setVisibility(View.VISIBLE);

		initializeDrawer();
	}

	public void onResume() {
		initializeLayoutType(drawerLayout);
	}

	public boolean isDrawerOpen() {
		return mNavigationDrawerFragment.isDrawerOpen();
	}

	public void setDrawerLayoutTypeNotifier(IDrawerLayoutTypeNotifier drawerLayoutTypeNotifier) {
		this.drawerLayoutTypeNotifier = drawerLayoutTypeNotifier;
	}

	public void setDrawerLayoutSaisonNotifier(IDrawerLayoutSaisonNotifier drawerLayoutSaisonNotifier) {
		this.drawerLayoutSaisonNotifier = drawerLayoutSaisonNotifier;
	}

	public void setValue(List<NavigationDrawerData> value) {
		mNavigationDrawerFragment.setValue(value);
	}

	public void updValue() {
		mNavigationDrawerFragment.updValue();
	}

	private void initializeDrawer() {
		drawerLayout = activity.findViewById(R.id.drawer_layout);

		mNavigationDrawerFragment = (NavigationDrawerFragment) activity.getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) drawerLayout);

		NavigationDrawerData header = new NavigationDrawerAdapter.NavigationDrawerData(0, R.layout.fragment_navigation_drawer_header_saison, notiferSaison);
		NavigationDrawerData footer = new NavigationDrawerAdapter.NavigationDrawerData(1, R.layout.fragment_navigation_drawer_footer_type, new NavigationDrawerTypeNotifer());
		mNavigationDrawerFragment.setHeader(header);
		mNavigationDrawerFragment.setFooter(footer);
	}

	private void initializeLayoutType(View view) {
		view = (view.getParent()==null) ? view : ((View)view.getParent());
		typeManager.initializeActivity(container, true);
		switch(typeManager.getType()) {
			case COMPETITION: {
				((LinearLayout)view.findViewById(R.id.ll_type_match)).setAlpha(1f);
				((LinearLayout)view.findViewById(R.id.ll_type_training)).setAlpha(.2f);
			}
			break;

			case TRAINING:
			default: {
				((LinearLayout)view.findViewById(R.id.ll_type_match)).setAlpha(.2f);
				((LinearLayout)view.findViewById(R.id.ll_type_training)).setAlpha(1f);
			}
			break;
		}
		if (drawerLayoutTypeNotifier != null) {
			drawerLayoutTypeNotifier.onDrawerLayoutTypeChange(typeManager.getType());
		}
	}

	private final class NavigationDrawerTypeNotifer implements NavigationDrawerNotifer {

		@Override
		public void onCreateView(View view) {
			LinearLayout llMatch = (LinearLayout)view.findViewById(R.id.ll_type_match);
			LinearLayout llTraining = (LinearLayout)view.findViewById(R.id.ll_type_training);

			llMatch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					typeManager.setType(TYPE.COMPETITION);
					initializeLayoutType(drawerLayout);
				}
			});

			llTraining.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					typeManager.setType(TYPE.TRAINING);
					initializeLayoutType(drawerLayout);
				}
			});

			initializeLayoutType(view);
		}

		@Override
		public void onUpdateView(View view) {
		}
	}

	private final class NavigationDrawerSaisonNotifer implements NavigationDrawerNotifer {
		private CustomArrayAdapter<String> adpSaison;
		private Spinner spSaison;
		private Context context;
		private View btnAdd;
		private View btnDel;

		@Override
		public void onCreateView(View view) {
			context = view.getContext().getApplicationContext();
			btnAdd = view.findViewById(R.id.btn_add_saison);
			btnDel = view.findViewById(R.id.btn_del_saison);

			spSaison = (Spinner)view.findViewById(R.id.sp_saison);
			initializeSaisonList();
			initializeSaison();
			initializeSaisonButton();
		}

		@Override
		public void onUpdateView(View view) {
		}

		private void initializeSaisonButton() {
			Log.d(TAG, "initializeSaisonButton");
			
			btnAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					OnClickViewListener onClickOkListener = new OnClickViewListener() {
					
						@Override
						public void onClick(DialogInterface dialog, View view, int which) {
							CheckBox cbActivate = (CheckBox) view.findViewById(R.id.cb_activate);
							DatePicker datePicker = (DatePicker) view.findViewById(R.id.dp_saison_year);
							int year = datePicker.getYear();
							if (!business.isExistSaison(year)) {
								boolean active = cbActivate.isChecked();
								Saison saison = business.createSaison(year, active);
								typeManager.setSaison(saison);
			
								business.initializeDataSaison();
								notiferSaison.initializeSaison();
							} else {
								Toast.makeText(activity, R.string.error_message_saison_already_exist, Toast.LENGTH_LONG).show();
							}
						}
					};
			
					FactoryDialog.getInstance()
						.buildLayoutDialog(activity, onClickOkListener, null, R.string.dialog_saison_add_title, R.layout.dialog_saison_year_picker, R.id.ll_main)
						.show();
				}
			});

			btnDel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Saison saison = typeManager.getSaison();
								if (!business.isEmptySaison(saison)) {
									if (!business.isExistInviteSaison(saison)) {
										business.deleteSaison(saison);
										typeManager.reinitialize(DrawerManager.this.context, notificationMessage);
				
										business.initializeDataSaison();
										notiferSaison.initializeSaison();
										updValue();
									} else {
										Toast.makeText(activity, R.string.error_message_invite_exist_saison, Toast.LENGTH_LONG).show();
									}
								}
							}
						}
					};
			
					FactoryDialog.getInstance()
						.buildYesNoDialog(context, onClickListener, R.string.dialog_saison_del_title, R.string.dialog_saison_del_message)
						.show();
				}
			});

		}

		private void initializeSaisonList() {
			Log.d(TAG, "initializeSaisonList");
			adpSaison = new CustomArrayAdapter<String>(context, business.getListTxtSaisons());
			spSaison.setAdapter(adpSaison);

			spSaison.setOnItemSelectedListener(adpSaison.new OnItemSelectedListener<Saison>() {
				@Override
				public Saison getItem(int position) {
					return business.getListSaison().get(position);
				}

				@Override
				public boolean isHintItemSelected(Saison item) {
					return business.isEmptySaison(item);
				}

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Saison item) {
					typeManager.setSaison(business.getListSaison().get(position));
					if (drawerLayoutSaisonNotifier != null) {
						drawerLayoutSaisonNotifier.onDrawerLayoutSaisonChange(parent, view, position, id, item);
					}
				}
			});
		}

		public void initializeSaison() {
			Log.d(TAG, "initializeSaison");
			Saison saison = typeManager.getSaison();
			int position = 0;
			List<Saison> listSaison = business.getListSaison();
			for(Saison item : listSaison) {
				if (item.equals(saison)) {
					spSaison.setSelection(position, true);
					break;
				} else {
					position++;
				}
			}
			adpSaison.notifyDataSetChanged();
		}
	}

	public static interface IDrawerLayoutTypeNotifier {
		public void onDrawerLayoutTypeChange(TYPE type);
	}

	public static interface IDrawerLayoutSaisonNotifier {
		public void onDrawerLayoutSaisonChange(AdapterView<?> parent, View view, int position, long id, Saison item);
	}
}