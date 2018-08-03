package com.example.ccqzy;


import com.example.ccqzy.beans.User;

/**
 * 全局信息
 */
public class UserMessage {

	// 配置信息
	public static boolean isFistLogin = true;
	
	//是否需要刷新，
	public static boolean needRefreshAll = false;
	public static boolean needRefreshMy = false;
	public static boolean needRefreshCheck = false;
	public static boolean needRefreshRectify = false;
	
	public static Boolean needRefreshNotableList=false;
	public static Boolean needRefreshTtableList=false;
	
	public static Boolean needRefreshGroupNotableList=false;
	public static Boolean needRefreshGrouptableList=false;

	/**
	 * 用户信息
	 */
	private static User user;

	public static User getUserInfo() {
		if(user == null){
			user = new User();
			user.setTenancyName(QzyApplication.preferences.getString("TenancyName"));
			user.setUsernameOrEmailAddress(QzyApplication.preferences.getString("UsernameOrEmailAddress"));
			user.setPassword(QzyApplication.preferences.getString("Password"));
		}
		return user;
	}
	public static void setUserInfo(User user) {
		UserMessage.user = user;
		QzyApplication.preferences.putString("TenancyName",user.getTenancyName());
		QzyApplication.preferences.putString("UsernameOrEmailAddress",user.getUsernameOrEmailAddress());
		QzyApplication.preferences.putString("Password",user.getPassword());
		QzyApplication.preferences.flush();
	}
	public static void removeUserInfo() {
		
		UserMessage.user = null;
		QzyApplication.preferences.remove("TenancyName");
		QzyApplication.preferences.remove("UsernameOrEmailAddress");
		QzyApplication.preferences.remove("Password");
		QzyApplication.preferences.flush();
	}
}
