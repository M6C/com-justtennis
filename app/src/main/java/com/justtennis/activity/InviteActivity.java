package com.justtennis.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.justtennis.R;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.FragmentTool;
import com.justtennis.ui.fragment.InviteFragment;

public class InviteActivity extends AppCompatActivity {

	public static final String EXTRA_MODE = "MODE";
	public static final String EXTRA_INVITE = "INVITE";
	public static final String EXTRA_USER = "USER";
	public static final String EXTRA_PLAYER_ID = "PLAYER_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.invite2);

		TypeManager typeManager = TypeManager.getInstance();
		typeManager.initializeActivity(findViewById(R.id.layout_main), false);

		InviteFragment fragment = new InviteFragment();
		FragmentTool.replaceFragment(this, fragment, R.id.item_detail_container);
	}
}