package com.justtennis.tool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListTool {

	private ListTool() {
	}

	public static <C> C get(List<C> list, Object o, Comparator<Object> comparator) {
		C ret = null;
		if (list != null && list.size() > 0) {
			for(C c : list) {
				if (comparator.compare(c, o) == 0) {
					ret = c;
					break;
				}
			}
		}
		return ret;
	}

	public static <C> List<C> getList(List<C> list, Object o, Comparator<Object> comparator) {
		List<C> ret = new ArrayList<C>();
		if (list != null && list.size() > 0) {
			for(C c : list) {
				if (comparator.compare(c, o) == 0) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	public static <C> boolean contains(List<C> list, Object o, Comparator<Object> comparator) {
		boolean ret = false;
		if (list != null && list.size() > 0) {
			for(C c : list) {
				if (comparator.compare(c, o) == 0) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
}
