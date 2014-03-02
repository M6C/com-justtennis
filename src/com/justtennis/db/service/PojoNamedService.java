package com.justtennis.db.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.justtennis.domain.comparator.PojoNamedComparator;
import com.justtennis.domain.interfaces.IPojoNamed;

public class PojoNamedService {

	public <P extends IPojoNamed> void order(List<P> listPojo) {
		SortedSet<P> set = new TreeSet<P>(new PojoNamedComparator());
		set.addAll(listPojo);
		listPojo.clear();
		listPojo.addAll(set);
	}

	public <P extends IPojoNamed> String[] getNames(List<P> listPojo) {
		String[] listTxtPojo = new String[listPojo.size()];
		int i = 0;
		for(IPojoNamed pojo : listPojo) {
			listTxtPojo[i++] = pojo.getName();
		}
		return listTxtPojo;
	}
}