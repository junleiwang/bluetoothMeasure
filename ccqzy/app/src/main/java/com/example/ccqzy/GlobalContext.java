package com.example.ccqzy;

public class GlobalContext {
	
	/**
	 * 百度地图等百度相关API使用的key
	 */
	public static final String BAIDU_KEY = "ioa7sQSbloe5XZKQKQxrXIQp";
	/**推送与alias type*/
	public static final String ALIAS_TYPE = "qzyq";
	
	/**
	 * 默认请求隐患列表数据每次请求的数据条数
	 */
	public static final int HD_REQUEST_COUNT = 10;

	private static String HOST;
	private static String getHost(){
		if(HOST==null){
			HOST = QzyApplication.getAppContext().getResources().getString(R.string.server_host);
		}
		return HOST;
	}
	
	/**
	 * 根据id获取url
	 * @param id
	 * @return
	 */
	public static String getUrl(int id){
		return GlobalContext.getHost()+ QzyApplication.getAppContext().getResources().getString(id);
	}
	/**
	 * 根据Id获取String
	 */
	public static String getStr(int id){
		return QzyApplication.getAppContext().getResources().getString(id);
	}
	
	private static String filePre;
	public static String getFileUri(String fileName){
		if(filePre==null){
			filePre = GlobalContext.getHost()+QzyApplication.getAppContext().getString(R.string.url_fileDownload);
		}
		return filePre+"?file="+fileName;
	}

	
	public static String getFileName(String fileUri){
		filePre = GlobalContext.getHost()+QzyApplication.getAppContext().getString(R.string.url_fileDownload);
		String replace = fileUri.replace(fileUri, filePre);
		return replace;
	}
//	public static void destory(){
//		cookieStore = new BasicCookieStore();
//	}
}
