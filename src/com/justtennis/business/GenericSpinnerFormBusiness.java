package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.PojoNamedService;
import com.justtennis.domain.GenericDBPojoNamed;

public abstract class GenericSpinnerFormBusiness <DATA extends GenericDBPojoNamed> {

	@SuppressWarnings("unused")
	private static final String TAG = GenericSpinnerFormBusiness.class.getSimpleName();
	public static final String EXTRA_DATA = "EXTRA_DATA";

	private Context context;

	private GenericService<DATA> dataService;
	private PojoNamedService pojoNamedService;
	protected GenericSpinnerFormBusiness <? extends GenericDBPojoNamed> subBusiness;

	private DATA data = null;
	private List<DATA> listData = new ArrayList<DATA>();
	private List<String> listDataTxt = new ArrayList<String>();

	public abstract boolean isEmptyData(DATA pojo);
	protected abstract GenericService<DATA> initializeService(Context context, INotifierMessage notificationMessage);
	protected abstract DATA getEmptyData();
	protected abstract DATA getNewData();

	public GenericSpinnerFormBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		pojoNamedService = new PojoNamedService();
		dataService = initializeService(context, notificationMessage);
		initializeSubBusiness(context, notificationMessage);
	}

	@SuppressWarnings("unchecked")
	public void initializeData(Intent intent) {
		if (intent != null && intent.hasExtra(EXTRA_DATA)) {
			data = (DATA) intent.getSerializableExtra(EXTRA_DATA);
		} else {
			data = getNewData();
		}

		listData.clear();
		listData.addAll(getService().getList());
		pojoNamedService.order(listData);

		listData.add(0, getEmptyData());

		initializeTxt();
	}

	public DATA add(DATA pojo) {
		getService().createOrUpdate(pojo);
		return pojo;
	}

	public void delete(DATA pojo) {
		if (pojo!=null && !isEmptyData(pojo)) {
			getService().delete(pojo);
		}
	}

	public int getPosition(DATA pojo) {
		return getPojoPosition(listData, pojo);
	}

	public void save() {
	}

	public GenericService<DATA> getService() {
		return dataService;
	}

	public GenericSpinnerFormBusiness<? extends GenericDBPojoNamed> getSubBusiness() {
		return subBusiness;
	}

	public DATA getData() {
		return data;
	}

	public List<DATA> getListData() {
		return listData;
	}

	public List<String> getListDataTxt() {
		return listDataTxt;
	}

	protected void initializeSubBusiness(Context context, INotifierMessage notificationMessage) {
	}

	protected void initializeTxt() {
		listDataTxt.clear();
		listDataTxt.addAll(pojoNamedService.getNames(listData));
	}

	protected Context getContext() {
		return context;
	}

	protected int getPojoPosition(List<DATA> listPojo, DATA pojo) {
		int ret=-1;
		for(int i=0 ; i<listPojo.size() ; i++) {
			if (listPojo.get(i).getId().equals(pojo.getId())) {
				ret = i;
				break;
			}
		}
		return ret;
	}
}