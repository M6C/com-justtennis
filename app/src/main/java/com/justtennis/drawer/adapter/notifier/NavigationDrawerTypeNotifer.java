package com.justtennis.drawer.adapter.notifier;

import android.view.View;
import android.widget.LinearLayout;

import com.justtennis.R;
import com.justtennis.drawer.manager.DrawerManager;
import com.justtennis.manager.TypeManager;

public final class NavigationDrawerTypeNotifer implements INavigationDrawerNotifer {

    private DrawerManager drawerManager;

    public NavigationDrawerTypeNotifer(DrawerManager drawerManager) {
        this.drawerManager = drawerManager;
    }

    @Override
    public void onCreateView(View view) {
        LinearLayout llTypeMatch = (LinearLayout)view.findViewById(R.id.ll_type_match);
        LinearLayout llTypeTraining = (LinearLayout)view.findViewById(R.id.ll_type_training);

        if (llTypeMatch != null && llTypeTraining != null) {
            llTypeMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerManager.getTypeManager().setType(TypeManager.TYPE.COMPETITION);
                    drawerManager.initializeLayoutType(drawerManager.getDrawerLayout());
                }
            });

            llTypeTraining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerManager.getTypeManager().setType(TypeManager.TYPE.TRAINING);
                    drawerManager.initializeLayoutType(drawerManager.getDrawerLayout());
                }
            });
        }

        drawerManager.initializeLayoutType(view);
    }

    @Override
    public void onUpdateView(View view) {
    }
}
