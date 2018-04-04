package com.cameleon.common.android.service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cameleon.common.android.model.GenericDBPojo;
import com.cameleon.common.android.model.GenericDBPojoNamed;
import com.cameleon.common.android.model.comparator.PojoNamedComparator;
import com.cameleon.common.android.model.interfaces.IPojoNamed;

public class PojoNamedService {

	public <P extends IPojoNamed> void order(List<P> listPojo) {
		SortedSet<P> set = new TreeSet<P>(new PojoNamedComparator());
		set.addAll(listPojo);
		listPojo.clear();
		listPojo.addAll(set);
	}

	public <P extends IPojoNamed> List<String> getNames(List<P> listPojo) {
		return getNames(listPojo, false);
	}

	public <P extends IPojoNamed> List<String> getNames(List<P> listPojo, boolean withId) {
		List<String> listTxtPojo = new ArrayList<String>();
		for(IPojoNamed pojo : listPojo) {
			String name = pojo.getName();
			if (withId && pojo instanceof GenericDBPojoNamed) {
				name = "[" + ((GenericDBPojo<?>)pojo).getId() + "] " + name;
			}
			listTxtPojo.add(name);
		}
		return listTxtPojo;
	}
}