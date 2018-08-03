package com.example.ccqzy.daos;

public interface AjaxCallBack<T> {

	public void onSuccess(T t);

	/**
	 * 失败的时候回调该方法
	 *
	 * @param showMessage
	 *            需要显示给用户看的内容
	 * @param detailMessage
	 *            异常的信息
	 */
	public void onFailed(String showMessage, String detailMessage);
	public void onLoading(long current, long total);
	/**
	 * 取消操作的方法
	 * @param //mes 需要显示给用户的信息
	 */
	public void onCancel();
	public void onStart();

}
