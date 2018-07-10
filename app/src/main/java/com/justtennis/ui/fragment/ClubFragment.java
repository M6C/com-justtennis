
package com.justtennis.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.justtennis.R;
import com.justtennis.databinding.FragmentClubBinding;
import com.justtennis.domain.Club;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.business.ClubBusiness;


public class ClubFragment extends Fragment {

	private ClubBusiness business;

	private EditText tvClubName;
	private EditText tvAddressLine1;
	private EditText tvAddressPostalCode;
	private EditText tvAddressCity;
	private FragmentActivity activity;

	public static ClubFragment build() {
		ClubFragment fragment = new ClubFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public static ClubFragment build(Club club) {
		ClubFragment fragment = new ClubFragment();
		Bundle args = new Bundle();
		args.putSerializable(ClubBusiness.EXTRA_DATA, club);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		activity = getActivity();
		assert activity != null;
		Context context = activity.getApplicationContext();
		assert context != null;

		business = new ClubBusiness(context, NotifierMessageLogger.getInstance());
		business.onCreate(this);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentClubBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_club, container, false);

		binding.setClub(business.getClub());
		binding.setAddress(business.getAddress());

		initializeViewById(binding.getRoot());
		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();
		initializeFab();
	}

	private void initializeFab() {
		FragmentTool.initializeFabDrawable(activity, FragmentTool.INIT_FAB_IMAGE.VALIDATE);
		FragmentTool.onClickFab(activity, v -> this.onClickValidate());
	}

	private void onClickValidate() {
		business.validate();
		FragmentTool.finish(activity);
	}

	protected void initializeViewById(View rootView) {
		tvClubName = rootView.findViewById(R.id.et_club_name);
		tvAddressLine1 = rootView.findViewById(R.id.et_address_line_1);
		tvAddressPostalCode = rootView.findViewById(R.id.et_address_postal_code);
		tvAddressCity = rootView.findViewById(R.id.et_address_city);
	}
}