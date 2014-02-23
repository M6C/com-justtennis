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
			List<Invite> result = filterData(constraint);
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

	private List<Invite> filterData(CharSequence constraint) {
		String filterValue = constraint.toString();
		int filterLength = filterValue.length();
		String idPlayer = null;
		String type = null;
		int idx = filterValue.indexOf(";");
		if (idx > 0) {
			idPlayer = filterValue.substring(0, idx);
		}
		if (idx < (filterLength - 1)) {
			type = filterValue.substring(idx+1, filterLength);
		}
		List<Invite> result = new ArrayList<Invite>();
		for(Invite person : valueOld) {
			if (
				(idPlayer == null || idPlayer.equals(person.getPlayer().getId().toString())) &&
				(type == null || type.equals(person.getPlayer().getType().toString()))
			) {
				result.add(person);
			}
		}
		return result;
	}
}