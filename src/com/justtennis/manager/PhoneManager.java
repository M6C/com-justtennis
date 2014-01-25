package com.justtennis.manager;

import java.util.List;

import android.app.Activity;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;

import com.justtennis.domain.Phone;
import com.justtennis.manager.mapper.PhoneMapper;

public class PhoneManager extends GenericCursorManager<Phone, PhoneMapper> {

	private static PhoneManager instance = null;

	public static PhoneManager getInstance() {
		if (instance == null) {
			instance = new PhoneManager();
		}
		return instance;
	}

	public List<Phone> getListPhone(Activity context, long idContact) {
	    String where = CommonDataKinds.Phone.CONTACT_ID + " = ?"; 
	    String[] whereParameters = new String[]{Long.toString(idContact)};
		return getList(context, where, whereParameters);
	}
	
	@Override
	protected CursorLoader buildCursorLoader(Activity context, String where, String[] whereParameters) {
		Uri uri = CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = PhoneMapper.getInstance().getListColumn();
		String sortOrder = null;

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected PhoneMapper getMapper() {
		return PhoneMapper.getInstance();
	}
}