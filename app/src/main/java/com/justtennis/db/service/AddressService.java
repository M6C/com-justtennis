package com.justtennis.db.service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.android.model.comparator.PojoNamedComparator;
import com.justtennis.db.sqlite.datasource.DBAddressDataSource;
import com.justtennis.domain.Address;

public class AddressService extends GenericService<Address> {

	public static final long ID_EMPTY_ADDRESS = -2l;

	public AddressService(Context context, INotifierMessage notificationMessage) {
		super(context, new DBAddressDataSource(context, notificationMessage), notificationMessage);
	}

	public void order(List<Address> listRanking) {
		SortedSet<Address> setRanking = new TreeSet<Address>(new PojoNamedComparator());
		setRanking.addAll(listRanking);
		listRanking.clear();
		listRanking.addAll(setRanking);
	}

	public Address getEmptyAddress() {
		Address ret = new Address();
		ret.setId(ID_EMPTY_ADDRESS);
		return ret;
	}

	public boolean isEmptyAddress(Address address) {
		return address.getId()!=null && ID_EMPTY_ADDRESS==address.getId();
	}
	
	public boolean isRealAddress(Address address) {
		return address.getId()!=null && address.getId() > 0;
	}
}