package com.justtennis.db.service;

import java.util.ArrayList;
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

	public <P extends IPojoNamed> List<String> getNames(List<P> listPojo) {
		List<String> listTxtPojo = new ArrayList<String>();
		for(IPojoNamed pojo : listPojo) {
			listTxtPojo.add(pojo.getName());
		}
		return listTxtPojo;
	}
}