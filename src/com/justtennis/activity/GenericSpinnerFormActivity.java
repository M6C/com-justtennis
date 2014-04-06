package com.justtennis.activity;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.business.GenericSpinnerFormBusiness;
import com.justtennis.domain.Address;
import com.justtennis.domain.GenericDBPojo;
import com.justtennis.domain.GenericDBPojoNamedSubId;

public abstract class GenericSpinnerFormActivity <P extends GenericDBPojoNamedSubId> extends Activity {

	private static final String TAG = GenericSpinnerFormActivity.class.getSimpleName();

	private static final int RESULT_FORM_LIST = 1;

	public enum MODE {
		SELECTION, ADD
	};

	public static final String EXTRA_DATA = "EXTRA_DATA";
	public static final String EXTRA_MODE = "EXTRA_MODE";
	public static final String EXTRA_OUT_LOCATION = "EXTRA_OUT_LOCATION";

	private GenericSpinnerFormBusiness<P, ?> business;
	private IGenericSpinnerFormResource resource;

	private int visibilitySelection = View.VISIBLE;
	private int visibilityContent = View.VISIBLE;
	private MODE mode = MODE.SELECTION;

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
	protected abstract GenericSpinnerFormBusiness<P, ?> getBusiness();
	protected abstract View buildFormAdd(ViewGroup llForm);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		setContentView(R.layout.generic_spinner_form);

		tvTitle = (TextView)findViewById(R.id.tv_title);
		etName = (EditText)findViewById(R.id.et_name);
		spList = (Spinner)findViewById(R.id.sp_list);
		spListForm = (Spinner)findViewById(R.id.sp_list_form);
		llSelection = findViewById(R.id.ll_selection);
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
		initializeFormAdd();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		initializeData();
		manageVisibility();
	}

	@Override
	public void onBackPressed() {
		onClickCancel(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		if (resultCode == RESULT_OK) {
			if (resultCode == RESULT_FORM_LIST) {
				@SuppressWarnings("unchecked")
				GenericDBPojo<Long>  pojo = (GenericDBPojo<Long>) data.getSerializableExtra(EXTRA_OUT_LOCATION);
				business.getData().setSubId(pojo != null ? pojo.getId() : null);
//				business.getSubBusiness().setData(pojo);
				setMode(MODE.ADD);
			}
		}
	}

	public void onClickValidate(View view) {
	}

	public void onClickCancel(View view) {
		finish();
	}

	public void onClickAdd(View view) {
		mode = MODE.ADD;
		manageVisibility();
	}

	public void onClickDelete(View view) {
		delete(business);
	}

	public void onClickDeleteFormList(View view) {
		delete(business.getSubBusiness());

		if (business.getSubBusiness().isEmptyData()) {
			business.getData().setSubId(null);
			business.saveData();
			
		}
	}

	public void onClickAddValidate(View view) {
		if (mode == MODE.ADD) {
			updateData(business.getData());
			business.saveData();
		}
		returnResult();
	}

	public void onClickAddFormList(View view) {
		Class<?> cls = resource.getFormListActivityClass();
		if (cls != null) {
			Intent intent = new Intent(this, cls);
			intent.putExtra(EXTRA_MODE, MODE.ADD);
	
			if (business.getData().getSubId() != null) {
				@SuppressWarnings("unchecked")
				List<Address> list = (List<Address>) business.getSubBusiness().getService().getList(new String[]{business.getData().getSubId().toString()});
				if (list != null && list.size() > 0) {
					intent.putExtra(LocationAddressActivity.EXTRA_DATA, list.get(0));
				}
			}
	
			startActivityForResult(intent, RESULT_FORM_LIST);
		}
	}

	protected void initializeFormAdd() {
		View formAdd = buildFormAdd(llForm);
		if (formAdd != null) {
			llForm.addView(formAdd);
		}
	}

	protected void initializeResource() {
		tvTitle.setText(resource.getTitleStringId());
		etName.setHint(resource.getNameHintStringId());
	}

	protected void initializeData() {
		Log.i(TAG, "initializeData");
		Intent intent = getIntent();
		business.initializeData(intent);

		initializeAdapter();
		initializeListener();
		initializeList();
	}

	protected void initializeAdapter() {
		adapterData = new CustomArrayAdapter<String>(this, business.getListDataTxt());
		if (business.getSubBusiness() != null) {
			adapterDataForm = new CustomArrayAdapter<String>(this, business.getSubBusiness().getListDataTxt());
		}
	}

	protected void initializeList() {
		if (adapterDataForm != null) {
			spListForm.setAdapter(adapterDataForm);
		}

		spList.setAdapter(adapterData);
		spList.setSelection(business.getPosition(), true);
	}

	protected void initializeField() {
		Log.i(TAG, "initializeField");
		etName.setText(business.isEmptyData() ? "" : business.getData().getName());
		if (business.getSubBusiness() != null) {
			spListForm.setSelection(business.getSubBusiness().getPosition(), true);
		}
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
				business.setData(data);
				initializeField();
			}
		});

		if (adapterDataForm != null) {
			spListForm.setOnItemSelectedListener(adapterDataForm.new OnItemSelectedListener<GenericDBPojoNamedSubId>() {
				@Override
				public GenericDBPojoNamedSubId getItem(int pojo) {
					return business.getSubBusiness().getListData().get(pojo);
				}
				
				@Override
				public boolean isHintItemSelected(GenericDBPojoNamedSubId pojo) {
					return false;//business.getSubBusiness() != null ? business.getSubBusiness().isEmptyData(pojo) : false;
				}
	
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id, GenericDBPojoNamedSubId data) {
					business.getData().setSubId(data.getId());
				}
			});
		}
	}
	
	protected String getText(EditText editText) {
		String text = editText.getText().toString();
		if ("".equals(text)) {
			text = null;
		}
		return text;
	}

	protected void updateData(P data) {
		data.setName(etName.getText().toString());
		if (business.getSubBusiness() != null) {
			int position = spListForm.getSelectedItemPosition();
			GenericDBPojo<Long> subItem = (GenericDBPojo<Long>)business.getSubBusiness().getListData().get(position);
			data.setSubId(subItem.getId());
		}
	}

	private void manageVisibility() {
		visibilitySelection = (mode == MODE.SELECTION ? View.VISIBLE : View.GONE);
		visibilityContent = (mode == MODE.SELECTION ? View.GONE : View.VISIBLE);

		llSelection.setVisibility(visibilitySelection);
		llAdd.setVisibility(visibilityContent);
		if (business.getSubBusiness() != null) {
			llListForm.setVisibility(View.VISIBLE);
		} else {
			llListForm.setVisibility(View.GONE);
		}
	}

	private void returnResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_OUT_LOCATION, business.getData());
		setResult(Activity.RESULT_OK, data);

		finish();
	}

	private void delete(final GenericSpinnerFormBusiness<?, ?> business) {
		if (!business.isEmptyData()) {
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					business.deleteData();
				}
			};
			FactoryDialog.getInstance()
				.buildOkCancelDialog(this, listener, R.string.dialog_location_delete_title, R.string.dialog_location_delete_message)
				.show();
		}
	}

	public interface IGenericSpinnerFormResource {
		public int getTitleStringId();
		public int getNameHintStringId();
		public Class<?> getFormListActivityClass();
	}

	public MODE getMode() {
		return mode;
	}
	public void setMode(MODE mode) {
		this.mode = mode;
	}
}