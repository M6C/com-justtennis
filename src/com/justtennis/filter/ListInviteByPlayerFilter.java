package com.justtennis.filter;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

import com.justtennis.domain.Invite;

public class ListInviteByPlayerFilter extends Filter {

	private IValueNotifier valueNotifier;
	private List<Invite> valueOld;

	public ListInviteByPlayerFilter(IValueNotifier valueNotifier, List<Invite> valueOld) {
		super();
		this.valueNotifier = valueNotifier;
		this.valueOld = valueOld;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults ret = new FilterResults();
		if (constraint!=null) {
			List<Invite> result = new ArrayList<Invite>();
			for(Invite person : valueOld) {
				if (constraint.equals(person.getPlayer().getId().toString())) {
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
		valueNotifier.setValue((List<Invite>) results.values);
	}
	
	public interface IValueNotifier {
		public void setValue(List<Invite> value);
	}
}