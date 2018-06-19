package com.justtennis.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.justtennis.domain.Player;

import java.io.Serializable;

public class PlayerViewModel extends ViewModel implements Serializable {
    private final MutableLiveData<Player> selected = new MutableLiveData<>();

    public void select(Player item) {
        selected.setValue(item);
    }

    public LiveData<Player> getSelected() {
        return selected;
    }
}