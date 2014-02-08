package com.justtennis.adapter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.domain.Contact;
import com.justtennis.domain.Person;
import com.justtennis.filter.ListPersonFilter;
import com.justtennis.manager.ContactManager;

public class ListPersonAdapter extends ArrayAdapter<Person> {

	@SuppressWarnings("unused")
	private static final String TAG = ListPersonAdapter.class.getSimpleName();

	private List<Person> value;
	private List<Person> valueOld;
	private ListPersonActivity activity;
	private Filter filter = null;

	
	public ListPersonAdapter(ListPersonActivity activity, List<Person> list) {
		super(activity, R.layout.list_person_row, android.R.id.text1, list);

		this.activity = activity;
		this.valueOld = new ArrayList<Person>(list);
		this.filter = new ListPersonFilter(new ListPersonFilter.IValueNotifier() {

			@Override
			public void setValue(List<Person> value) {
				ListPersonAdapter.this.value.clear();
				ListPersonAdapter.this.value.addAll(value);
				notifyDataSetChanged();
			}
		}, valueOld);
	}

	@Override
	public Filter getFilter() {
		if (filter!=null) {
			return filter;
		} else {
			return super.getFilter();
		}
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public static class ViewHolder {
		public Person person;
		public TextView firstname;
		public TextView lastname;
		public ImageView imagePerson;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Person v = value.get(position);
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_person_row, null);
		}
		rowView.setTag(v);

		TextView firstname = (TextView) rowView.findViewById(R.id.tv_fistname);
		TextView lastname = (TextView) rowView.findViewById(R.id.tv_lastname);
		ImageView imagePerson = (ImageView) rowView.findViewById(R.id.iv_person);

		imagePerson.setImageResource(R.drawable.player_unknow_2);
		if (v instanceof Contact) {
			Bitmap photo = ContactManager.getInstance().getPhoto(activity, v.getId());
			if (photo!=null) {
				imagePerson.setImageBitmap(photo);
			}
		}

		firstname.setText(v.getFirstName());
		lastname.setText(v.getLastName());

		if (ApplicationConfig.SHOW_ID) {
			firstname.setText(firstname.getText() + " [" + v.getId() + "]");
		}
	    return rowView;
	}

	public List<Person> getValue() {
		return value;
	}

	public void setValue(List<Person> value) {
		this.value = value;

		valueOld.clear();
		valueOld.addAll(this.value);
	}
}