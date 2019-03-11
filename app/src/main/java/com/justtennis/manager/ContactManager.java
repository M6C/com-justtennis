package com.justtennis.manager;

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

import com.cameleon.common.android.manager.GenericCursorManager;
import com.justtennis.domain.Contact;
import com.justtennis.manager.mapper.ContactMapper;
import com.justtennis.tool.ToolPermission;

import java.io.InputStream;
import java.util.List;

public class ContactManager extends GenericCursorManager<Contact, ContactMapper> {

	private static ContactManager instance = null;

	private ContactManager(){
	}

	public static ContactManager getInstance() {
		if (instance == null) {
			instance = new ContactManager();
		}
		return instance;
	}

	public List<Contact> getListContact(Context context) {
	    String where = String.format("%s = ?", Contacts.IN_VISIBLE_GROUP);
	    String[] whereParameters = new String[]{"1"};
		return getList(context, where, whereParameters);
	}

	public Bitmap getPhoto(Activity activity, Long contactId) {
		ContentResolver contentResolver = activity.getContentResolver();

		if (!ToolPermission.checkPermissionREAD_CONTACTS(activity, true)) {
			return null;
		}
		
		Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);

	    // contactPhotoUri --> content://com.android.contacts/contacts/1557
	    InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri); // <-- always null
		return BitmapFactory.decodeStream(photoDataStream);
	}

	@Override
	protected CursorLoader buildCursorLoader(Context context, String where, String[] whereParameters) {
		// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = ContactMapper.getInstance().getListColumn();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

		return new CursorLoader(context, uri, projection, null, null, sortOrder);
	}

	@Override
	protected ContactMapper getMapper() {
		return ContactMapper.getInstance();
	}
}