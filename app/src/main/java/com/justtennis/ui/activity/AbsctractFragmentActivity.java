
package com.justtennis.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.justtennis.R;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.FragmentTool;

public abstract class AbsctractFragmentActivity extends AppCompatActivity {

	private static final String TAG = AbsctractFragmentActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.abstract_fragment);

		TypeManager typeManager = TypeManager.getInstance();
		typeManager.initializeActivity(findViewById(R.id.layout_main), false);

		Fragment fragment = createFragment();
		FragmentTool.replaceFragment(this, fragment, R.id.main_container);
	}

	protected abstract Fragment createFragment();
}