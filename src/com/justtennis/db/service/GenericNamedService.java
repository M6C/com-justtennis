package com.justtennis.db.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.android.model.GenericDBPojoNamed;

public class GenericNamedService<POJO extends GenericDBPojoNamed> extends GenericService<POJO> {

	public GenericNamedService(Context context, GenericDBDataSource<POJO> dbDataSource, INotifierMessage notificationMessage) {
		super(context, dbDataSource, notificationMessage);
	}

	public List<String> getListName(List<POJO> list) {
		List<String> ret = new ArrayList<String>();
		for(POJO pojo : list) {
			ret.add(pojo.getName());
		}
		return ret ;
	}

	public List<String> getListName() {
		return getListName(getList());
	}
}
