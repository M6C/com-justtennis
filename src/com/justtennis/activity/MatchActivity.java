package com.justtennis.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerData;
import com.justtennis.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;
import com.justtennis.business.MainBusiness;
import com.justtennis.fragment.NavigationDrawerFragment;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.R;

public class MatchActivity extends Activity implements INotifierMessage {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		NavigationDrawerNotifer notiferSaison = new NavigationDrawerNotifer() {

			@Override
			public void onCreateView(View view) {
				Context context = view.getContext().getApplicationContext();
				INotifierMessage notificationMessage = NotifierMessageLogger.getInstance();
				final MainBusiness business = new MainBusiness(context, notificationMessage);
				business.initializeData();

				Spinner spSaison = (Spinner)view.findViewById(R.id.sp_saison);
				CustomArrayAdapter<String> adpSaison = new CustomArrayAdapter<String>(context, new ArrayList<String>());
				spSaison.setAdapter(adpSaison);
				adpSaison.notifyDataSetChanged();
			}

			@Override
			public void onUpdateView(View view) {
			}
		};

		List<NavigationDrawerData> value = new ArrayList<NavigationDrawerAdapter.NavigationDrawerData>();
		value.add(new NavigationDrawerAdapter.NavigationDrawerData(0, R.layout.fragment_navigation_drawer_header_saison, notiferSaison));

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		mNavigationDrawerFragment.setValue(value);
	}

//	@Override
//	public void onNavigationDrawerItemSelected(int position) {
//		// update the main content by replacing fragments
//		FragmentManager fragmentManager = getFragmentManager();
//		fragmentManager
//				.beginTransaction()
//				.replace(R.id.container,
//						PlaceholderFragment.newInstance(position + 1)).commit();
//	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.match, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_match,
					container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			if (activity instanceof MatchActivity) {
				((MatchActivity) activity).onSectionAttached(getArguments().getInt(
						ARG_SECTION_NUMBER));
			}
		}
	}

	@Override
	public void notifyError(Exception ex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

}
