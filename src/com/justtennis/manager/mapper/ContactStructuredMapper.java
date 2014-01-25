package com.justtennis.manager.mapper;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.justtennis.domain.Contact;


public class ContactStructuredMapper extends GenericMapper<Contact> {
	
	private static ContactStructuredMapper instance = null;
	//StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME
	private static final String[] LIST_COLUMN = new String[] {
		ContactsContract.CommonDataKinds.StructuredName._ID, 
		ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY, 
		ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, 
		ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, 
		ContactsContract.CommonDataKinds.StructuredName.HAS_PHONE_NUMBER
	};

	protected ContactStructuredMapper() {
	}

	public static ContactStructuredMapper getInstance() {
		if (instance==null)
			instance = new ContactStructuredMapper();
		return instance;
	}

	@Override
	protected Contact cursorToPojo(Cursor cursor) {
	    Contact ret = new Contact();
        ret.setId(mappeLong(cursor, ContactsContract.CommonDataKinds.StructuredName._ID));
	    ret.setLookup(mappeString(cursor, ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY));
	    ret.setFirstName(mappeString(cursor, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
	    ret.setLastName(mappeString(cursor, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
	    ret.setHasPhone(mappeBoolean(cursor, ContactsContract.CommonDataKinds.StructuredName.HAS_PHONE_NUMBER));
	    return ret;
	}

	@Override
	public String[] getListColumn() {
		return LIST_COLUMN;
	}
}