package com.justtennis.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.Filter;

import com.justtennis.domain.Person;

public class ListPersonFilter extends Filter {

	private IValueNotifier valueNotifier;
	private List<Person> valueOld;

	public ListPersonFilter(IValueNotifier valueNotifier, List<Person> valueOld) {
		super();
		this.valueNotifier = valueNotifier;
		this.valueOld = valueOld;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults ret = new FilterResults();
		if (constraint!=null) {
			String txt = constraint.toString().toUpperCase(Locale.FRANCE);
			List<Person> result = new ArrayList<Person>();
			for(Person person : valueOld) {
				boolean chk = false;
				if (person.getFirstName()!=null) {
					chk = person.getFirstName().toUpperCase(Locale.FRANCE).contains(txt);
				}
				if (!chk && person.getLastName()!=null) {
					chk = person.getLastName().toUpperCase(Locale.FRANCE).contains(txt);
				}
				if (chk) {
					result.add(person);
				}
			}
			ret.values = result;
			ret.count = result.size();
		} else {
			ret.values = valueOld;
			ret.count = valueOld.size();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		valueNotifier.setValue((List<Person>) results.values);
	}
	
	public interface IValueNotifier {
		public void setValue(List<Person> value);
	}
}
