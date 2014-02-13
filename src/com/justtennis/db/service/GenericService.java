package com.justtennis.db.service;

import java.util.List;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.GenericDBDataSource;
import com.justtennis.domain.GenericDBPojo;

public class GenericService<POJO extends GenericDBPojo<Long>> {

	protected GenericDBDataSource<POJO> dbDataSource;
	protected Context context;

	public GenericService(Context context, GenericDBDataSource<POJO> dbDataSource, INotifierMessage notificationMessage) {
		this.context = context;
		this.dbDataSource = dbDataSource;
	}

	public long getCount() {
    	try {
    		dbDataSource.open();
    		return dbDataSource.count();
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

    /**
     * 
     * @param pojo
     */
	public void createOrUpdate(POJO pojo) {
    	try {
    		dbDataSource.open();
    		// Save in database
    		if (pojo.getId()==null) {
				dbDataSource.create(pojo);
    		}
    		else {
				dbDataSource.update(pojo);
    		}
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public POJO find(long id) {
		POJO ret = null;
    	try {
    		dbDataSource.open();
			// Save in database
			ret = dbDataSource.getById(id);
    	}
    	finally {
    		dbDataSource.close();
    	}
    	return ret;
	}

	public List<POJO> getList() {
    	try {
    		dbDataSource.open();
    		return dbDataSource.getAll();
    	}
    	finally {
    		dbDataSource.close();
    	}
	}
	
	public List<POJO> getList(String[] id) {
		try {
			dbDataSource.open();
			return dbDataSource.getInId(id);
		}
		finally {
			dbDataSource.close();
		}
	}

	public void delete(POJO pojo) {
    	try {
    		dbDataSource.open();
    		dbDataSource.delete(pojo);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}
}
