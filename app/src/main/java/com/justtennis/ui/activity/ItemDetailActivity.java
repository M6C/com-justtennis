package com.justtennis.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.ListCompetitionActivity;
import com.justtennis.activity.ListInviteActivity;
import com.justtennis.activity.ListPlayerActivity;
import com.justtennis.activity.MainActivity;
import com.justtennis.activity.PieChartActivity;
import com.justtennis.business.MainBusiness;
import com.justtennis.manager.TypeManager;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.DBFeedTool;
import com.justtennis.ui.fragment.ItemDetailFragment;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private MainBusiness business;

    private int currentBottomNavigationItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void onClickMatch() {
        Intent intent = new Intent(getApplicationContext(), InviteActivity.class);
        intent.putExtra(InviteActivity.EXTRA_PLAYER_ID, business.getUnknownPlayerId());
        intent.putExtra(InviteActivity.EXTRA_MODE, InviteActivity.MODE.INVITE_SIMPLE);
        startActivity(intent);
    }
}
