package com.justtennis.ui.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.cameleon.common.android.model.GenericDBPojo;

import java.io.Serializable;

public class GenericPojoViewModel <D extends GenericDBPojo> extends ViewModel implements Serializable {
    private final MutableLiveData<D> selected = new MutableLiveData<>();

    public void select(D item) {
        selected.setValue(item);
    }

    public LiveData<D> getSelected() {
        return selected;
    }
}