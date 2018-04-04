package com.cameleon.common.android.inotifier;

public interface INotifierMessage {
	   
    public void notifyError(Exception ex);

    public void notifyMessage(String msg);
}
