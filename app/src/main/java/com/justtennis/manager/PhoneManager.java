package com.justtennis.manager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;

import com.cameleon.common.android.manager.GenericCursorManager;
import com.justtennis.domain.Phone;
import com.justtennis.manager.mapper.PhoneMapper;

public class PhoneManager extends GenericCursorManager<Phone, PhoneMapper> {

	private static PhoneManager instance = null;

	public static PhoneManager getInstance(Context context) {
		if (instance == null) {
			instance = new PhoneManager();
		}
		return instance;
	}

	public List<Phone> getListPhone(Activity context, long idContact) {
	    String where = String.format("%s = ?", CommonDataKinds.Phone.CONTACT_ID);
	    String[] whereParameters = new String[]{Long.toString(idContact)};
		return getList(context, where, whereParameters);
	}
	
	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		Uri uri = CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = PhoneMapper.getInstance().getListColumn();
		String sortOrder = null;

		return new CursorLoader(context, uri, projection, null, null, sortOrder);
	}

	@Override
	protected PhoneMapper getMapper() {
		return PhoneMapper.getInstance();
	}
}