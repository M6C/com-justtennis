package com.justtennis.ui.fragment.placeholder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justtennis.R;
import com.justtennis.ui.activity.ItemDetailActivity;
import com.justtennis.ui.activity.UiMainActivity;

public class ItemDetailPlaceHolderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ItemDetailPlaceHolderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(int sectionNumber) {
        ItemDetailPlaceHolderFragment fragment = new ItemDetailPlaceHolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        return rootView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((ItemDetailActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//    }
}