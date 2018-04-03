package com.justtennis.drawer.data;

import com.justtennis.drawer.adapter.NavigationDrawerAdapter.NavigationDrawerNotifer;

public class NavigationDrawerData {
	private long id;
	private int layout;
	private NavigationDrawerNotifer notifer;

	public NavigationDrawerData(long id, int layout, NavigationDrawerNotifer notifer) {
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
	public NavigationDrawerNotifer getNotifer() {
		return notifer;
	}
	public void setNotifer(NavigationDrawerNotifer notifer) {
		this.notifer = notifer;
	}
}
