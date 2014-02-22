package com.justtennis.filter;

import java.util.ArrayList;
import java.util.List;

import android.widget.Filter;

import com.justtennis.domain.Player;

public class ListPlayerByTypeFilter extends Filter {

	private IValueNotifier valueNotifier;
	private List<Player> valueOld;

	public ListPlayerByTypeFilter(IValueNotifier valueNotifier, List<Player> valueOld) {
		super();
		this.valueNotifier = valueNotifier;
		this.valueOld = valueOld;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults ret = new FilterResults();
		if (constraint!=null) {
			List<Player> result = new ArrayList<Player>();
			for(Player person : valueOld) {
				if (constraint.equals(person.getType().toString())) {
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
		valueNotifier.setValue((List<Player>) results.values);
	}
	
	public interface IValueNotifier {
		public void setValue(List<Player> value);
	}
}