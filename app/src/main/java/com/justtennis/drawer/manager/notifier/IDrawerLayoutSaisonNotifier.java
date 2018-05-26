package com.justtennis.drawer.manager.notifier;

import android.view.View;
import android.widget.AdapterView;

import com.justtennis.domain.Saison;

public interface IDrawerLayoutSaisonNotifier {
    public void onDrawerLayoutSaisonChange(AdapterView<?> parent, View view, int position, long id, Saison item);
}
