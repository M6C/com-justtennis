package com.cameleon.common.android.inotifier;

public interface INotifierSessionProgress {

	/**
	 * 
	 * @param messageId
	 */
	public void notifySendSession(int messageId);

	/**
	 * 
	 * @param messageId
	 * @param count
	 * @param total
	 */
	public void notifySendLocation(int messageId, int count, int total);

	/**
	 * 
	 * @param messageId
	 */
	public void notifySendEnd(int messageId);

}
