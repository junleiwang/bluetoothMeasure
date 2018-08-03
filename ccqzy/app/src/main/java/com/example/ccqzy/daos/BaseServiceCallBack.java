package com.example.ccqzy.daos;

public abstract class
BaseServiceCallBack<T> implements ServiceCallBack<T> {
	public void onStart() {}
	public void onFailed(int type, String showMessage, String detailMessage) {}
	public void onLoading(long current, long total) {}
	public void onSuccess(T t) {}
	public void onCancel() {}
}
