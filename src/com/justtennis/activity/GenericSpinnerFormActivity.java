package com.justtennis.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.GenericSpinnerFormBusiness;
import com.justtennis.domain.GenericDBPojoNamed;

public abstract class GenericSpinnerFormActivity <P extends GenericDBPojoNamed> extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = GenericSpinnerFormActivity.class.getSimpleName();

	public enum MODE {
		SELECTION, ADD
	};

	public static final String EXTRA_DATA = "EXTRA_DATA";
	public static final String EXTRA_MODE = "EXTRA_MODE";
	public static final String EXTRA_OUT_LOCATION = "EXTRA_OUT_LOCATION";

	private GenericSpinnerFormBusiness<P> business;
	private IGenericSpinnerFormResource resource;

	private int visibilitySelection = View.VISIBLE;
	private int visibilityContent = View.VISIBLE;
	private MODE mode = MODE.SELECTION;

	private LinearLayout llContent;

	private TextView tvTitle;
	private EditText etName;
	private View llAdd;
	private LinearLayout llForm;
	private LinearLayout llListForm;
	private View llSelection;
	private View ivDelete;

	private Spinner spList;
	private Spinner spListForm;
	private CustomArrayAdapter<String> adapterData;
	private CustomArrayAdapter<String> adapterDataForm;

	public abstract IGenericSpinnerFormResource getResource();
	public abstract void onClickValidate(View view);
	protected abstract GenericSpinnerFormBusiness<P> getBusiness();
	protected abstract View buildFormAdd();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generic_spinner_form);

		tvTitle = (TextView)findViewById(R.id.tv_title);
		etName = (EditText)findViewById(R.id.et_name);
		spList = (Spinner)findViewById(R.id.sp_list);
		spListForm = (Spinner)findViewById(R.id.sp_list_form);
		llSelection = findViewById(R.id.ll_selection);
		llContent = (LinearLayout)findViewById(R.id.ll_content);

		llAdd = findViewById(R.id.ll_add);
		llForm = (LinearLayout)findViewById(R.id.ll_form);
		llListForm = (LinearLayout)findViewById(R.id.ll_list_form);
		ivDelete = findViewById(R.id.iv_delete);

		business = getBusiness();
		resource = getResource();

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(EXTRA_MODE)) {
				mode = (MODE) intent.getSerializableExtra(EXTRA_MODE);
			}
		}
		initializeResource();
		initializeViewForm(llForm);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeData();
		manageVisibility();
	}
	
	public void onClickCancel(View view) {
		finish();
	}

	public void onClickAdd(View view) {
		mode = MODE.ADD;
		manageVisibility();

		View formAdd = buildFormAdd();
		if (formAdd != null) {
			llForm.addView(formAdd);
		}
	}

	public void onClickDelete(View view) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		};
		FactoryDialog.getInstance()
			.buildOkCancelDialog(this, listener, R.string.dialog_location_address_delete_title, R.string.dialog_location_address_delete_message)
			.show();
	}

	public void onClickAddValidate(View view) {
	}

	public void onClickAddFormList(View view) {
	}
	
	public void onClickDeleteFormList(View view) {
	}

	protected void initializeViewForm(LinearLayout layout) {
		llForm.setVisibility(View.GONE);
	}

	protected void initializeResource() {
		tvTitle.setText(resource.getTitleStringId());
		etName.setHint(resource.getNameHintStringId());
	}

	protected void initializeData() {
		Intent intent = getIntent();
		business.initializeData(intent);

		initializeList();
		initializeListForm();
		initializeField();
		initializeListener();
	}

	private void manageVisibility() {
		visibilitySelection = (mode == MODE.SELECTION ? View.VISIBLE : View.GONE);
		visibilityContent = (mode == MODE.SELECTION ? View.GONE : View.VISIBLE);

		llSelection.setVisibility(visibilitySelection);
//		llContent.setVisibility(visibilityContent);
		llAdd.setVisibility(visibilityContent);
	}
	protected void initializeList() {
		adapterData = new CustomArrayAdapter<String>(this, business.getListDataTxt());
		spList.setAdapter(adapterData);
	}

	protected void initializeListForm() {
		if (business.getSubBusiness() != null) {
			adapterDataForm = new CustomArrayAdapter<String>(this, business.getSubBusiness().getListDataTxt());
			spListForm.setAdapter(adapterDataForm);
			llListForm.setVisibility(View.VISIBLE);
		} else {
			llListForm.setVisibility(View.GONE);
		}
	}

	protected void initializeField() {
		etName.setText(business.getData().getName());
	}

	protected void initializeListener() {
		spList.setOnItemSelectedListener(adapterData.new OnItemSelectedListener<P>() {
			@Override
			public P getItem(int position) {
				return business.getListData().get(position);
			}

			@Override
				public boolean isHintItemSelected(P address) {
				return business.isEmptyData(address);
			}
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id, P data) {
			}
		});
	}
	
	protected String getText(EditText editText) {
		String text = editText.getText().toString();
		if ("".equals(text)) {
			text = null;
		}
		return text;
	}

	public interface IGenericSpinnerFormResource {
		public int getTitleStringId();
		public int getNameHintStringId();
	}
}