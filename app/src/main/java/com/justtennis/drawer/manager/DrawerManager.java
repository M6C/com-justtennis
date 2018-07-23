package com.justtennis.drawer.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.R;
import com.justtennis.domain.User;
import com.justtennis.drawer.adapter.notifier.NavigationDrawerSaisonNotifer;
import com.justtennis.drawer.adapter.notifier.NavigationDrawerTypeNotifer;
import com.justtennis.drawer.data.NavigationDrawerData;
import com.justtennis.drawer.fragment.NavigationDrawerFragment;
import com.justtennis.drawer.manager.business.DrawerSaisonBusiness;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutSaisonNotifier;
import com.justtennis.drawer.manager.notifier.IDrawerLayoutTypeNotifier;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.ResourceTool;

import java.util.List;
import java.util.Objects;

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
	private DrawerSaisonBusiness business;

	public DrawerManager(Activity activity, INotifierMessage notificationMessage) {
		this.notificationMessage = notificationMessage;
		this.context = activity.getApplicationContext();
		this.activity = activity;

		this.business = new DrawerSaisonBusiness(context, notificationMessage);
		this.notiferSaison = new NavigationDrawerSaisonNotifer(this);
		this.typeManager = TypeManager.getInstance(context, notificationMessage);

		business.initializeDataSaison();
	}

	/**
	 * For Activity
	 * @param layoutResId
	 */
	public void setContentView(int layoutResId) {
		activity.setContentView(R.layout.generic_main_drawer);

		container = (ViewGroup) activity.findViewById(R.id.container);

		LayoutInflater inflater = ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View footerView =  inflater.inflate(layoutResId, null, false);

        container.addView(footerView);
		container.setVisibility(View.VISIBLE);

		initializeDrawer();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.drawer_ui_main, container, false);
//        mDrawerListView = (ListView)rootView.findViewById(R.id.nav_lv);

		NavigationView navView = rootView.findViewById(R.id.nav_view);

		View headerView = navView.getHeaderView(0);
		Spinner spSaison = headerView.findViewById(R.id.sp_saison);
		if (spSaison != null) {
			spSaison.setAdapter(new ArrayAdapter<>(
					Objects.requireNonNull(getActivity()).getApplicationContext(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1,
					getBusiness().getListTxtSaisons()));
		}

		TextView tvUserName = headerView.findViewById(R.id.nav_header_user_name);
		if (tvUserName != null) {
			User user =  business.getCurrentUser();
			if (user != null) {
				tvUserName.setText(user.getFullName());
			}
		}
		ImageView ivUser = headerView.findViewById(R.id.iv_user);
		if (ivUser != null) {
			ivUser.setImageResource(ResourceTool.getIdDrawableInIntArray(activity, R.array.list_ic_tennis_player));
		}
//		initializeDrawer();

		return rootView;
	}

	public void onResume() {
		if (drawerLayout != null) {
			initializeLayoutType(drawerLayout);
		}
	}

	public boolean isDrawerOpen() {
		return (mNavigationDrawerFragment == null) ? false : mNavigationDrawerFragment.isDrawerOpen();
	}

	public void close() {
		if (mNavigationDrawerFragment != null) {
			mNavigationDrawerFragment.close();
		}
	}

	public void setDrawerLayoutTypeNotifier(IDrawerLayoutTypeNotifier drawerLayoutTypeNotifier) {
		this.drawerLayoutTypeNotifier = drawerLayoutTypeNotifier;
	}

	public void setDrawerLayoutSaisonNotifier(IDrawerLayoutSaisonNotifier drawerLayoutSaisonNotifier) {
		this.drawerLayoutSaisonNotifier = drawerLayoutSaisonNotifier;
	}

	public void setValue(List<NavigationDrawerData> value) {
		if (mNavigationDrawerFragment != null) {
			mNavigationDrawerFragment.setValue(value);
		}
	}

	public void updValue() {
		if (mNavigationDrawerFragment != null) {
			mNavigationDrawerFragment.updValue();
		}
	}

	private void initializeDrawer() {
		drawerLayout = activity.findViewById(R.id.drawer_layout);

		if (drawerLayout != null) {
			mNavigationDrawerFragment = (NavigationDrawerFragment) activity.getFragmentManager().findFragmentById(R.id.navigation_drawer);
			mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) drawerLayout);

			NavigationDrawerData header = new NavigationDrawerData(0, R.layout.fragment_navigation_drawer_header_saison, notiferSaison);
			NavigationDrawerData footer = new NavigationDrawerData(1, R.layout.fragment_navigation_drawer_footer_type, new NavigationDrawerTypeNotifer(this));
			mNavigationDrawerFragment.setHeader(header);
			mNavigationDrawerFragment.setFooter(footer);
		}
	}

	public void initializeLayoutType(View view) {
		view = (view.getParent()==null) ? view : ((View)view.getParent());
		typeManager.initializeActivity(container, true);
		LinearLayout llTypeMatch = (LinearLayout)view.findViewById(R.id.ll_type_match);
		LinearLayout llTypeTraining = (LinearLayout)view.findViewById(R.id.ll_type_training);
		if (llTypeMatch != null && llTypeTraining != null) {
			switch(typeManager.getType()) {
				case COMPETITION: {
					llTypeMatch.setAlpha(1f);
					llTypeTraining.setAlpha(.2f);
				}
				break;
	
				case TRAINING:
				default: {
					llTypeMatch.setAlpha(.2f);
					llTypeTraining.setAlpha(1f);
				}
				break;
			}
		}
		if (drawerLayoutTypeNotifier != null) {
			drawerLayoutTypeNotifier.onDrawerLayoutTypeChange(typeManager.getType());
		}
	}

	public Activity getActivity() {
		return activity;
	}

	public DrawerSaisonBusiness getBusiness() {
		return business;
	}

	public TypeManager getTypeManager() {
		return typeManager;
	}

	public View getDrawerLayout() {
		return drawerLayout;
	}

	public NavigationDrawerSaisonNotifer getNotiferSaison() {
		return notiferSaison;
	}

	public IDrawerLayoutSaisonNotifier getDrawerLayoutSaisonNotifier() {
		return drawerLayoutSaisonNotifier;
	}

	public INotifierMessage getNotificationMessage() {
		return notificationMessage;
	}

}