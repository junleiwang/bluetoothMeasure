package com.example.ccqzy.daos;

/**
 * 服务回调接口
 * @author hxak
 *
 * @param <T>
 */
public interface ServiceCallBack<T> {
	
	public static final int ERROR_TYPE_NET = 1;
	public static final int ERROR_TYPE_SESSION_TIMEOUT = 2;
	public static final int ERROR_TYPE_NODATA = 3;
	public void onStart();
	/**
	 * 失败的时候回调该方法
	 * 
	 * @param showMessage
	 *            需要显示给用户看的内容
	 * @param detailMessage
	 *            异常的信息
	 */
	public void onFailed(int type, String showMessage, String detailMessage);
	/**
	 * 同步完成回调的方法
	 * 
	 * @param t
	 */
	public void onSuccess(T t);
	public void onLoading(long current, long total);
	public void onCancel();
}
