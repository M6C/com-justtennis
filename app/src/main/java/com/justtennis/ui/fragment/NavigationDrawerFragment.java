package com.justtennis.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.justtennis.R;
import com.justtennis.domain.User;
import com.justtennis.drawer.manager.business.DrawerSaisonBusiness;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.tool.ResourceTool;
import com.justtennis.ui.rxjava.RxNavigationDrawer;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.Objects;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentActivity mActivity;
    private Context mContext;
    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private Spinner mSpSaison;
    private Button mButtonTypeTraining;
    private Button mButtonTypeMatch;
    private Button mButtonTypeTrainingDisable;
    private Button mButtonTypeMatchDisable;
    private ArrayAdapter<String> mAdapterSaison;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private DrawerSaisonBusiness saisonBusiness;
    private TypeManager mTypeManager;

    private NavigationView navView;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotifierMessageLogger notifier = NotifierMessageLogger.getInstance();
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        saisonBusiness = new DrawerSaisonBusiness(mContext, notifier);
        saisonBusiness.initializeData();
        mTypeManager = TypeManager.getInstance(mContext, notifier);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drawer_ui_main, container, false);

        initializeMenu(rootView);
        initializeSaison(rootView);
        initializeUser(rootView);
        initializeType(rootView);
        initializeSubscribeDbRestored();
//        initializeSubscribeFragment();

        return rootView;
    }

    private void initializeUser(View rootView) {
        NavigationView navView = rootView.findViewById(R.id.nav_view);

        View headerView = navView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.nav_header_user_name);
        if (tvUserName != null) {
            User user =  saisonBusiness.getCurrentUser();
            if (user != null) {
                tvUserName.setText(user.getFullName());
            }
        }
        ImageView ivUser = headerView.findViewById(R.id.iv_user);
        if (ivUser != null) {
            ivUser.setImageResource(ResourceTool.getIdDrawableInIntArray(mActivity, R.array.list_ic_tennis_player));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        managerVisibilityType(mTypeManager.getType());

        saisonBusiness.onResume();
        mAdapterSaison.notifyDataSetChanged();
        //RxFragment.publish(RxFragment.SUBJECT_ON_SHOW, TAG);
    }

    @Override
    public void onDestroy() {
        RxNavigationDrawer.unregister(this);
        super.onDestroy();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = Objects.requireNonNull(getActivity()).findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(() -> mDrawerToggle.syncState());

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Spinner getSaisonSpinner() {
        return mSpSaison;
    }

    private void initializeMenu(View rootView) {
        navView = rootView.findViewById(R.id.nav_menu);
        navView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void initializeSaison(View rootView) {
        NavigationView navView = rootView.findViewById(R.id.nav_view);

        mSpSaison = navView.getHeaderView(0).findViewById(R.id.sp_saison);
        if (mSpSaison != null) {
            mAdapterSaison = new ArrayAdapter<>(
                    Objects.requireNonNull(getActivity()).getApplicationContext(),
                    R.layout.spinner_saison_item,
                    android.R.id.text1,
                    saisonBusiness.getListTxtSaisons());
            mAdapterSaison.setDropDownViewResource(R.layout.spinner_saison_dropdown_item);
            mSpSaison.setAdapter(mAdapterSaison);
            mSpSaison.setSelection(saisonBusiness.getSaisonActivePosition());

            mSpSaison.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    RxNavigationDrawer.publish(RxNavigationDrawer.SUBJECT_SELECT_SAISON, saisonBusiness.getListSaison().get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    RxNavigationDrawer.publish(RxNavigationDrawer.SUBJECT_SELECT_SAISON, saisonBusiness.getEmptySaison());
                }
            });
        }
    }

    private void initializeType(View navView) {
        NavigationView navigationDrawerBottom = navView.findViewById(R.id.navigation_drawer_bottom);
        if (navigationDrawerBottom != null) {
            mButtonTypeTraining = navigationDrawerBottom.findViewById(R.id.btn_type_entrainement);
            mButtonTypeTrainingDisable = navigationDrawerBottom.findViewById(R.id.btn_type_entrainement_disable);
            mButtonTypeMatch = navigationDrawerBottom.findViewById(R.id.btn_type_match);
            mButtonTypeMatchDisable = navigationDrawerBottom.findViewById(R.id.btn_type_match_disable);

            mButtonTypeTrainingDisable.setOnClickListener(v -> RxNavigationDrawer.publish(RxNavigationDrawer.SUBJECT_CHANGE_TYPE, TypeManager.TYPE.TRAINING));
            mButtonTypeMatchDisable.setOnClickListener(v -> RxNavigationDrawer.publish(RxNavigationDrawer.SUBJECT_CHANGE_TYPE, TypeManager.TYPE.COMPETITION));
        }
    }

    private void initializeSubscribeDbRestored() {
        RxNavigationDrawer.subscribe(RxNavigationDrawer.SUBJECT_DB_RESTORED, this, o -> {
            saisonBusiness.initializeData();
            mAdapterSaison.notifyDataSetChanged();

            // Select 1st Saison Active
            AdapterView.OnItemSelectedListener onItemSelectedListener = mSpSaison.getOnItemSelectedListener();
            mSpSaison.setOnItemSelectedListener(null);
            mSpSaison.setSelection(saisonBusiness.getSaisonActivePosition());
            mSpSaison.setOnItemSelectedListener(onItemSelectedListener);
        });
    }

//    private void initializeSubscribeFragment() {
//        RxFragment.subscribe(RxFragment.SUBJECT_ON_SHOW, this, o -> {
//            String tag = (String)o;
//
//            navView.setNavigationItemSelectedListener(null);
//
//            int id = 0;
//            if (tag.equals(UserFragment.TAG)) {
//                id = R.id.nav_user;
//            } else if (tag.equals(ListClubFragment.TAG)) {
//                id = R.id.nav_list_club;
//            } else if (tag.equals(PalmaresFastFragment.TAG)) {
//                id = R.id.nav_palmares_fast;
//            }
//
//            if (id != 0) {
//                View user = navView.findViewById(R.id.nav_user);
//                if (user != null) {
//                    user.setSelected(id == R.id.nav_user);
//                }
//                View club = navView.findViewById(R.id.nav_list_club);
//                if (club != null) {
//                    club.setSelected(id == R.id.nav_list_club);
//                }
//                View palmares = navView.findViewById(R.id.nav_palmares_fast);
//                if (palmares != null) {
//                    palmares.setSelected(id == R.id.nav_palmares_fast);
//                }
//            }
//
//            navView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
//        });
//    }

    private void managerVisibilityType(TypeManager.TYPE o) {
        if (TypeManager.TYPE.TRAINING == o) {
            mButtonTypeTraining.setVisibility(View.VISIBLE);
            mButtonTypeTrainingDisable.setVisibility(View.GONE);
            mButtonTypeMatch.setVisibility(View.GONE);
            mButtonTypeMatchDisable.setVisibility(View.VISIBLE);
        } else {
            mButtonTypeTraining.setVisibility(View.GONE);
            mButtonTypeTrainingDisable.setVisibility(View.VISIBLE);
            mButtonTypeMatch.setVisibility(View.VISIBLE);
            mButtonTypeMatchDisable.setVisibility(View.GONE);
        }
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
//        if (mDrawerListView != null) {
//            mDrawerListView.setItemChecked(position, true);
//        }
        closeDrawer();
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'mContext', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setTitle(R.string.app_name);
        }
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
    }

    private static void logMe(String msg) {
        com.crashlytics.android.Crashlytics.log(msg);
        Logger.logMe(TAG, msg);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int deep = 0;
        int i = item.getItemId();
        if (i == R.id.nav_user) {
            deep = 1;
            fragment = UserFragment.build();
        } else if (i == R.id.nav_list_club) {
            // Create List Club Fragment
            fragment = ListClubFragment.build();
        } else if (i == R.id.nav_palmares_fast) {
            fragment = PalmaresFastFragment.build();
        } else if (i == R.id.nav_sms_message) {
            deep = 1;
            fragment = SmsMessageFragment.build();
        }
        if (fragment != null) {
            FragmentTool.clearBackStackEntry(mActivity, deep);
            FragmentTool.replaceFragment(mActivity, fragment);
            closeDrawer();
        }
        return false;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
