package com.justtennis.manager.mapper;

import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;

import com.justtennis.domain.Contact;


public class ContactMapper extends GenericMapper<Contact> {
	
	private static ContactMapper instance = null;
	//StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME
	private static final String[] LIST_COLUMN = null;
//	new String[] {
//		Contacts._ID,
//		Contacts.LOOKUP_KEY,
//		Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? 
//				Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
//				Contacts.HAS_PHONE_NUMBER
//	};

	protected ContactMapper() {
	}

	public static ContactMapper getInstance() {
		if (instance==null)
			instance = new ContactMapper();
		return instance;
	}

	@Override
	protected Contact cursorToPojo(Cursor cursor) {
	    Contact ret = new Contact();
        ret.setId(mappeLong(cursor, Contacts._ID));
	    ret.setLookup(mappeString(cursor, Contacts.LOOKUP_KEY));
	    ret.setFirstName(mappeString(cursor, Contacts.DISPLAY_NAME));
	    ret.setHasPhone(mappeBoolean(cursor, Contacts.HAS_PHONE_NUMBER));
	    return ret;
	}
	
	@Override
	protected String getAlternatifColumnNameWhenNoExist(String columnName) {
		if (StructuredName.GIVEN_NAME.equals(columnName)) {
			return Contacts.DISPLAY_NAME;
		} else if (StructuredName.FAMILY_NAME.equals(columnName)) {
			return null;
		} else {
			return super.getAlternatifColumnNameWhenNoExist(columnName);
		}
	}

	@Override
	public String[] getListColumn() {
		return LIST_COLUMN;
	}
}