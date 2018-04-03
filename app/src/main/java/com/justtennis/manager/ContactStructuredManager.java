package com.justtennis.manager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.ContactsContract;

import com.cameleon.common.android.manager.GenericCursorManager;
import com.justtennis.domain.Contact;
import com.justtennis.manager.mapper.ContactStructuredMapper;

public class ContactStructuredManager extends GenericCursorManager<Contact, ContactStructuredMapper> {

	private static ContactStructuredManager instance = null;

	public static ContactStructuredManager getInstance(Context context) {
		if (instance == null) {
			instance = new ContactStructuredManager();
		}
		return instance;
	}

	public List<Contact> getListContact(Activity context) {
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
	    String where = ContactsContract.CommonDataKinds.StructuredName.IN_VISIBLE_GROUP + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.MIMETYPE + " = ?"; 
	    String[] whereParameters = new String[]{"1", ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
		return getList(context, where, whereParameters);
	}

	public Contact getContact(Activity context, long idContact) {
		Contact ret = null;
//	    String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
	    String where = ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.MIMETYPE + " = ?"; 
	    String[] whereParameters = new String[]{Long.toString(idContact), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};//new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
		List<Contact> list = getList(context, where, whereParameters);
		if (list!=null && list.size()>0) {
			ret = list.get(0);
		}
		return ret;
	}

	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		Uri uri = ContactsContract.Data.CONTENT_URI;
		String[] projection = ContactStructuredMapper.getInstance().getListColumn();
		String sortOrder = null;

		return new CursorLoader(context, uri, projection, null, null, sortOrder);
	}

	@Override
	protected ContactStructuredMapper getMapper() {
		return ContactStructuredMapper.getInstance();
	}
}