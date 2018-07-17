package com.justtennis.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.activity.MainActivity;
import com.justtennis.activity.MessageActivity;
import com.justtennis.activity.PalmaresFastActivity;
import com.justtennis.business.MainBusiness;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.DBFeedTool;
import com.justtennis.tool.ExitTool;
import com.justtennis.tool.FragmentTool;
import com.justtennis.tool.ToolPermission;
import com.justtennis.ui.common.CommonEnum;
import com.justtennis.ui.fragment.ListInviteFragment;
import com.justtennis.ui.fragment.ListPlayerFragment;
import com.justtennis.ui.fragment.NavigationDrawerFragment;
import com.justtennis.ui.fragment.PieChartFragment;
import com.justtennis.ui.rxjava.RxNavigationDrawer;

import org.gdocument.gtracergps.launcher.log.Logger;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = ItemDetailActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NotifierMessageLogger notifier;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mBottomNavigation;

    private MainBusiness business;
    private TypeManager mTypeManager;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int currentBottomNavigationItem = 0;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);

        DBFeedTool.feed(getApplicationContext());

        notifier = NotifierMessageLogger.getInstance();
        business = new MainBusiness(this, notifier);
        mTypeManager = TypeManager.getInstance(this, notifier);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
        }

        initializeActionBar();
        initializeBottomNavigation();
        initializeSubscribeChangeType();
//        initializeSubscribeFragment();
    }

    @Override
    protected void onDestroy() {
        RxNavigationDrawer.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, @IdRes int resid, boolean first) {
        int id = TypeManager.getThemeResource();
        theme.applyStyle(id, true);
        super.onApplyThemeResource(theme, id, first);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.item_detail_container, ItemDetailPlaceHolderFragment.newInstance(position + 1))
//                .commit();

//        ItemDetailFragment fragment = new ItemDetailFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.item_detail_container, fragment)
//                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!drawerManager.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
//            setTitle(R.string.application_label);
        return true;
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUpTo(new Intent(this, ItemListActivity.class));
                break;
            case R.id.action_old_ihm:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.action_palmares_fast:
                onClickFastPalmares(null);
                break;
            case R.id.action_message:
                onClickMessage(null);
                break;
            case R.id.action_list_person:
                onClickListPerson(null);
                break;
            case R.id.action_send_apk:
                onClickSendApk(null);
                break;
            case R.id.action_send_db:
                onClickSendDb(null);
                break;
            case R.id.action_db_backup:
                onClickDBBackup(null);
                break;
            case R.id.action_db_restore:
                onClickDBRestore(null);
                break;
            default:
                ret = super.onOptionsItemSelected(item);
        }

        return ret;
    }

    @Override
    public void onBackPressed() {
        if (ExitTool.onBackPressedFragment(this)) {
            finish();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    private void initializeSubscribeChangeType() {
        RxNavigationDrawer.subscribe(RxNavigationDrawer.SUBJECT_CHANGE_TYPE, this, o -> {
            TypeManager.TYPE type = (TypeManager.TYPE)o;
            mTypeManager.setType(type);

            int id = TypeManager.getThemeResource(type);
            setTheme(id);

            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        });
    }

//    private void initializeSubscribeFragment() {
//        RxFragment.subscribe(RxFragment.SUBJECT_ON_SHOW, this, o -> {
//            String tag = (String)o;
//
//            int id = 0;
//            if (tag.equals(ListPlayerFragment.TAG)) {
//                id = R.id.navigation_player;
//            } else if (tag.equals(ListInviteFragment.TAG)) {
//                id = R.id.navigation_invite;
//            } else if (tag.equals(PieChartFragment.TAG)) {
//                id = R.id.navigation_statistic;
//            }
//
//            if (id > 0) {
//                mBottomNavigation.setOnNavigationItemSelectedListener(null);
//                mBottomNavigation.setSelectedItemId(id);
//                currentBottomNavigationItem = mBottomNavigation.getSelectedItemId();
//                mBottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
//            }
//        });
//    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(TypeManager.getThemeResource(), true);
        return theme;
    }

    private void onClickListInvite() {
//        Intent intent = null;
//        switch(TypeManager.getInstance().getType()) {
//            case COMPETITION:
//                intent = new Intent(getApplicationContext(), ListCompetitionActivity.class);
//                break;
//            case TRAINING:
//            default:
//                intent = new Intent(getApplicationContext(), ListInviteActivity.class);
//                break;
//        }
//        startActivity(intent);
//        if (mBottomNavigation.getSelectedItemId() != R.id.navigation_invite) {
            ListInviteFragment fragment = ListInviteFragment.build();
            FragmentTool.replaceFragment(this, fragment);
//        }
    }

    private void onClickListPlayer() {
        ListPlayerFragment fragment = ListPlayerFragment.build(CommonEnum.LIST_FRAGMENT_MODE.EDIT);
        FragmentTool.replaceFragment(this, fragment);
    }

    private void onClickListStatistic() {
        PieChartFragment fragment = PieChartFragment.build();
        FragmentTool.replaceFragment(this, fragment);
    }

    public void onClickFastPalmares(View view) {
        Intent intent = new Intent(this, PalmaresFastActivity.class);
        startActivity(intent);
    }

    public void onClickMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        startActivity(intent);
    }

    public void onClickListPerson(View view) {
        Intent intent = new Intent(getApplicationContext(), ListPersonActivity.class);
        startActivity(intent);
    }

    public void onClickSendApk(View view) {
        post(() -> {
            OnClickSendApkListenerOk listener = new OnClickSendApkListenerOk(this);
            FactoryDialog.getInstance()
                    .buildOkCancelDialog(this, listener, R.string.dialog_send_apk_title, R.string.dialog_send_apk_message)
                    .show();
        });
    }

    public void onClickSendDb(View view) {
        post(() -> {
            OnClickSendDBListenerOk listener = new OnClickSendDBListenerOk(this);
            FactoryDialog.getInstance()
                    .buildOkCancelDialog(this, listener, R.string.dialog_send_db_title, R.string.dialog_send_db_message)
                    .show();
        });
    }

    public void onClickDBBackup(View view) {
        if (ToolPermission.checkPermissionWRITE_EXTERNAL_STORAGE(this)) {
            doDBBackup();
        }
    }

    public void onClickDBRestore(View view) {
        if (ToolPermission.checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            doDBRestore();
        }
    }

    private void initializeActionBar() {
        setSupportActionBar(mToolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerLayout.addDrawerListener(setupDrawerToggle());
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void initializeBottomNavigation() {
        mBottomNavigation = findViewById(R.id.navigationView);
        mBottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        mBottomNavigation.setSelectedItemId(R.id.navigation_invite);

        View toolbar = findViewById(R.id.toolbar_layout);//detail_toolbar

        ((AppBarLayout)toolbar.getParent()).addOnOffsetChangedListener((appBarLayout, verticalOffset) -> mBottomNavigation.setTranslationY(verticalOffset*-1));
    }

    private void doDBBackup() {
        post(() -> {
            OnClickDBBackupListenerOk listener = new OnClickDBBackupListenerOk(this);
            FactoryDialog.getInstance()
                    .buildOkCancelDialog(this, listener, R.string.dialog_backup_title, R.string.dialog_backup_message)
                    .show();
        });
    }

    private void doDBRestore() {
        post(() -> {
            OnClickDBRestoreListenerOk listener = new OnClickDBRestoreListenerOk(this);
            FactoryDialog.getInstance()
                    .buildOkCancelDialog(this, listener, R.string.dialog_restore_title, R.string.dialog_restore_message)
                    .show();
        });
    }

    private void post(Runnable o) {
        findViewById(R.id.container).postDelayed(o, 1000);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
//        if (currentBottomNavigationItem == item.getItemId()) {
//            return false;
//        }
        currentBottomNavigationItem = item.getItemId();
        switch (currentBottomNavigationItem) {
            case R.id.navigation_player:
                FragmentTool.clearBackStackEntry(this);
                onClickListPlayer();
                return true;
            case R.id.navigation_invite:
                FragmentTool.clearBackStackEntry(this);
                onClickListInvite();
                return true;
            case R.id.navigation_statistic:
                FragmentTool.clearBackStackEntry(this);
                onClickListStatistic();
                return true;
            default:
                return false;
        }
    }

    protected static void logMe(String msg) {
        Logger.logMe(TAG, msg);
    }
}
