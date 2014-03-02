package com.justtennis.db.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.justtennis.db.sqlite.datasource.DBAddressDataSource;
import com.justtennis.domain.Address;
import com.justtennis.domain.comparator.PojoNamedComparator;

public class AddressService extends GenericService<Address> {

	public AddressService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBAddressDataSource(context, notificationMessage), notificationMessage);
	}

	public void order(List<Address> listRanking) {
		SortedSet<Address> setRanking = new TreeSet<Address>(new PojoNamedComparator());
		setRanking.addAll(listRanking);
		listRanking.clear();
		listRanking.addAll(setRanking);
	}
}