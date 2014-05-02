package com.justtennis.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.activity.GenericSpinnerFormActivity;
import com.justtennis.db.service.GenericService;
import com.justtennis.db.service.PojoNamedService;
import com.justtennis.domain.Address;
import com.justtennis.domain.GenericDBPojoNamedSubId;

public abstract class GenericSpinnerFormBusiness <DATA extends GenericDBPojoNamedSubId, SUB_DATA extends GenericDBPojoNamedSubId> {

	@SuppressWarnings("unused")
	private static final String TAG = GenericSpinnerFormBusiness.class.getSimpleName();
	private Context context;
	private INotifierMessage notificationMessage;

	private GenericService<DATA> dataService;
	private PojoNamedService pojoNamedService;
	protected GenericSpinnerFormBusiness <SUB_DATA, ?> subBusiness;

	private DATA data = null;
	private List<DATA> listData = new ArrayList<DATA>();
	private List<String> listDataTxt = new ArrayList<String>();

	public abstract boolean isEmptyData(DATA pojo);
	protected abstract GenericService<DATA> initializeService(Context context, INotifierMessage notificationMessage);
	protected abstract GenericSpinnerFormBusiness<SUB_DATA, ?> initializeSubBusiness(Context context, INotifierMessage notificationMessage);
	protected abstract DATA getEmptyData();
	protected abstract DATA getNewData();

	public GenericSpinnerFormBusiness(Context context, INotifierMessage notificationMessage) {
		this.context = context;
		this.notificationMessage = notificationMessage;
		pojoNamedService = new PojoNamedService();
		dataService = initializeService(context, notificationMessage);
		subBusiness = initializeSubBusiness(context, notificationMessage);
	}

	@SuppressWarnings("unchecked")
	public void initializeData(Intent intent) {
		List<DATA> list = getService().getList();

		listData.clear();
		listData.addAll(list);
		pojoNamedService.order(listData);
		
		listData.add(0, getEmptyData());

		if (intent != null && intent.hasExtra(GenericSpinnerFormActivity.EXTRA_DATA)) {
			data = (DATA) intent.getSerializableExtra(GenericSpinnerFormActivity.EXTRA_DATA);
			if (data != null && data.getId() != null) {
				if (list.contains(data)) {
					for(DATA d : list) {
						if (data.equals(d)) {
							data = d;
							break;
						}
					}
				} else {
					data = dataService.find(data.getId());
				}
			}
		}
		if (data == null) {
			data = getNewData();
		}

		if (subBusiness != null) {
			Intent subIntent = new Intent();
			if (data.getSubId() != null) {
				SUB_DATA subData = subBusiness.getEmptyData();
				subData.setId(data.getSubId());
				subIntent.putExtra(GenericSpinnerFormActivity.EXTRA_DATA, subData);
			}
			subBusiness.initializeData(subIntent);
		}

		initializeTxt();
		initializeSubData();
	}

	public void initializeSubData() {
		initializeSubBusiness(context, notificationMessage);
	}

	public void saveData() {
		if (isEmptyData()) {
			data.setId(null);
		}
		add(data);
	}

	public void deleteData() {
		delete(data);
		data = getNewData();
	}
	
	public int getPosition() {
		return data==null ? 0 : getPosition(data);
	}

	public boolean isEmptyData() {
		return isEmptyData(data);
	}

	public DATA add(DATA pojo) {
		getService().createOrUpdate(pojo);
		return pojo;
	}

	public void delete(DATA pojo) {
		if (pojo!=null && !isEmptyData(pojo)) {
			getService().delete(pojo);

			int position = getPosition(pojo);
			if (position >= 0){
				listData.remove(position);
				listDataTxt.remove(position);
			}
		}
	}
	
	public int getPosition(DATA pojo) {
		return getPosition(pojo.getId());
	}
	
	public int getPosition(Long id) {
		return getPojoPosition(listData, id);
	}

	public GenericService<DATA> getService() {
		return dataService;
	}

	public GenericSpinnerFormBusiness<SUB_DATA, ?> getSubBusiness() {
		return subBusiness;
	}

	public DATA getData() {
		return data;
	}

	public void setData(DATA data) {
		this.data = data;
		if (data != null) {
			setSubDataById(data.getSubId());
		}
	}

	public void setSubDataById(Long subId) {
		if (data != null) {
			data.setSubId(subId);
		}
		if (subBusiness != null) {
			subBusiness.setData(subId == null ? null : subBusiness.getService().find(subId));
		}
	}

	public List<DATA> getListData() {
		return listData;
	}

	public List<String> getListDataTxt() {
		return listDataTxt;
	}

	protected void initializeTxt() {
		listDataTxt.clear();
		listDataTxt.addAll(pojoNamedService.getNames(listData));
	}

	protected Context getContext() {
		return context;
	}

	protected int getPojoPosition(List<DATA> listPojo, Long id) {
		int ret=-1;
		for(int i=0 ; i<listPojo.size() ; i++) {
			if (listPojo.get(i).getId().equals(id)) {
				ret = i;
				break;
			}
		}
		return ret;
	}
}