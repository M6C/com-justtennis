package com.justtennis.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.io.Serializable;

public class ScoreViewModel extends ViewModel implements Serializable {
    private final MutableLiveData<String[][]> selected = new MutableLiveData<>();

    public void select(String[][] item) {
        selected.setValue(item);
    }

    public LiveData<String[][]> getSelected() {
        return selected;
    }
}