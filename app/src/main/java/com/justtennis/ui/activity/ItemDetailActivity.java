package com.justtennis.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.MainActivity;
import com.justtennis.activity.MessageActivity;
import com.justtennis.activity.PalmaresFastActivity;
import com.justtennis.activity.PieChartActivity;
import com.justtennis.business.MainBusiness;
import com.justtennis.listener.ok.OnClickDBBackupListenerOk;
import com.justtennis.listener.ok.OnClickDBRestoreListenerOk;
import com.justtennis.listener.ok.OnClickSendApkListenerOk;
import com.justtennis.listener.ok.OnClickSendDBListenerOk;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.DBFeedTool;
import com.justtennis.tool.ToolPermission;
import com.justtennis.ui.fragment.ItemDetailFragment;
import com.justtennis.ui.fragment.NavigationDrawerFragment;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private MainBusiness business;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int currentBottomNavigationItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        DBFeedTool.feed(getApplicationContext());

        business = new MainBusiness(this, NotifierMessageLogger.getInstance());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMatch();
            }
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.navigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (currentBottomNavigationItem == item.getItemId()) {
                return false;
            }
            currentBottomNavigationItem = item.getItemId();
            switch (currentBottomNavigationItem) {
                case R.id.navigation_player: {
                    onClickListPlayer();
                    return true;
                }
                case R.id.navigation_invite: {
                    onClickListInvite();
                    return true;
                }
                case R.id.navigation_statistic: {
                    onClickListStatistic();
                    return true;
                }
                default:
                    return false;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.navigation_invite);

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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
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
        ItemDetailFragment fragment = new ItemDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit();
    }

    private void onClickListPlayer() {
        Intent intent = new Intent(getApplicationContext(), ListPlayerActivity.class);
        intent.putExtra(ListPlayerActivity.EXTRA_MODE, ListPlayerActivity.MODE.EDIT);
        startActivity(intent);
    }

    private void onClickListStatistic() {
        Intent intent = new Intent(getApplicationContext(), PieChartActivity.class);
        startActivity(intent);
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
        OnClickSendApkListenerOk listener = new OnClickSendApkListenerOk(this);
        FactoryDialog.getInstance()
                .buildOkCancelDialog(getApplicationContext(), listener, R.string.dialog_send_apk_title, R.string.dialog_send_apk_message)
                .show();
    }

    public void onClickSendDb(View view) {
        OnClickSendDBListenerOk listener = new OnClickSendDBListenerOk(this);
        FactoryDialog.getInstance()
                .buildOkCancelDialog(getApplicationContext(), listener, R.string.dialog_send_db_title, R.string.dialog_send_db_message)
                .show();
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

    private void doDBBackup() {
        OnClickDBBackupListenerOk listener = new OnClickDBBackupListenerOk(this);
        FactoryDialog.getInstance()
                .buildOkCancelDialog(getApplicationContext(), listener, R.string.dialog_backup_title, R.string.dialog_backup_message)
                .show();
    }

    private void doDBRestore() {
        OnClickDBRestoreListenerOk listener = new OnClickDBRestoreListenerOk(this);
        FactoryDialog.getInstance()
                .buildOkCancelDialog(getApplicationContext(), listener, R.string.dialog_restore_title, R.string.dialog_restore_message)
                .show();
    }

    private void onClickMatch() {
        Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
        intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
        intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_SIMPLE);
        startActivity(intent);
    }
}
