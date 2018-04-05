package com.justtennis.db.sqlite.datasource;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.service.SaisonService;
import com.justtennis.db.sqlite.helper.DBPersonHelper;
import com.justtennis.db.sqlite.helper.DBPlayerHelper;
import com.justtennis.domain.Person;
import com.justtennis.domain.Saison;
import com.justtennis.manager.TypeManager;

public abstract class DBPersonDataSource<P extends Person> extends GenericDBDataSourceByType<P> {

	private static final String TAG = DBPersonDataSource.class.getCanonicalName();

	public DBPersonDataSource(DBPersonHelper dbPersonHelper, INotifierMessage notificationMessage) {
		super(dbPersonHelper, notificationMessage);
	}

	public List<P> getLikeByName(String str) {
		return query("(" +
			DBPersonHelper.COLUMN_FIRSTNAME + " like '%" + str + "%' OR " + 
			DBPersonHelper.COLUMN_LASTNAME + " like '%" + str + "%'" +
		") ");
	}

	@Override
	protected String customizeWhere(String where) {
		where = super.customizeWhere(where);

		where = customizeWhereSaison(where);
		return where;
	}

	protected void putPersonValue(ContentValues values, Person person) {
		values.put(DBPersonHelper.COLUMN_FIRSTNAME, person.getFirstName());
		values.put(DBPersonHelper.COLUMN_LASTNAME, person.getLastName());
		values.put(DBPersonHelper.COLUMN_BIRTHDAY, person.getBirthday());
		values.put(DBPersonHelper.COLUMN_PHONENUMBER, person.getPhonenumber());
		values.put(DBPersonHelper.COLUMN_ADDRESS, person.getAddress());
		values.put(DBPersonHelper.COLUMN_POSTALCODE, person.getPostalCode());
		values.put(DBPersonHelper.COLUMN_LOCALITY, person.getLocality());
	}

	protected String customizeWhereSaison(String where) {
		Saison saison = TypeManager.getInstance().getSaison();
		if (saison != null && saison.getId() != null && !SaisonService.isEmpty(saison)) {
			if (where != null) {
				where += " AND ";
			} else {
				where = " ";
			}
			where += "(" + DBPlayerHelper.COLUMN_ID_SAISON + " = " + saison.getId() + " OR " + DBPlayerHelper.COLUMN_ID_SAISON + " IS NULL)";
		}
		return where;
	}

	protected int cursorToPojo(Cursor cursor, P person, int col) {
		col = super.cursorToPojo(cursor, person, col);
		person.setFirstName(cursor.getString(col++));
		person.setLastName(cursor.getString(col++));
		person.setBirthday(cursor.getString(col++));
		person.setPhonenumber(cursor.getString(col++));
		person.setAddress(cursor.getString(col++));
		person.setPostalCode(cursor.getString(col++));
		person.setLocality(cursor.getString(col++));
		return col;
	}

	@Override
	protected String getTag() {
		return TAG;
	}
}