package com.justtennis.manager;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.justtennis.domain.Contact;
import com.justtennis.manager.mapper.ContactMapper;

public class ContactManager extends GenericCursorManager<Contact, ContactMapper> {

	private static ContactManager instance = null;

	public static ContactManager getInstance() {
		if (instance == null) {
			instance = new ContactManager();
		}
		return instance;
	}

	public List<Contact> getListContact(Activity context) {
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + ("1") + "'";
	    String where = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ?"; 
	    String[] whereParameters = new String[]{"1"};
		return getList(context, where, whereParameters);
	}

	public Bitmap getPhoto(Context context, Long contactId) {
		ContentResolver contentResolver = context.getContentResolver();
		
		Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);

	    // contactPhotoUri --> content://com.android.contacts/contacts/1557

	    InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri); // <-- always null
	    Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
	    return photo;
	}

	@Override
	protected CursorLoader buildCursorLoader(Activity context, String where, String[] whereParameters) {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = ContactMapper.getInstance().getListColumn();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		return new CursorLoader(context, uri, projection, where, whereParameters, sortOrder);
	}

	@Override
	protected ContactMapper getMapper() {
		return ContactMapper.getInstance();
	}
}