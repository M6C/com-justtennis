package com.justtennis.drawer.data;

import com.justtennis.drawer.adapter.notifier.INavigationDrawerNotifer;

public class NavigationDrawerData {
	private long id;
	private int layout;
	private INavigationDrawerNotifer notifer;

	public NavigationDrawerData(long id, int layout, INavigationDrawerNotifer notifer) {
		this.id = id;
		this.layout = layout;
		this.notifer = notifer;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getLayout() {
		return layout;
	}
	public void setLayout(int layout) {
		this.layout = layout;
	}
	public INavigationDrawerNotifer getNotifer() {
		return notifer;
	}
	public void setNotifer(INavigationDrawerNotifer notifer) {
		this.notifer = notifer;
	}
}
