package com.justtennis.ui.fragment.placeholder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.ui.activity.UiMainActivity;

public class UiMainPlaceHolderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public UiMainPlaceHolderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(int sectionNumber) {
        UiMainPlaceHolderFragment fragment = new UiMainPlaceHolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ui_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((UiMainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}