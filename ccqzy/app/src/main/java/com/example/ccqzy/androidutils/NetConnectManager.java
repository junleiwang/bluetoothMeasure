package com.example.ccqzy.androidutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.util.Log;

import java.util.Hashtable;

/**
 * @author 小金人
 * @date 2012-04-02
 * @QQ 574613441
 */
public class NetConnectManager {
	public static boolean netAvaliable = true;
	private static final String LOG_TAG = "NetConnectManager" ;
	
    /**  
     * 中国移动cmwap  
     */  
    public static String CMWAP = "cmwap";
       
    /**  
     * 中国移动cmnet  
     */  
    public static String CMNET = "cmnet";
       
    //中国联通3GWAP设置        中国联通3G因特网设置        中国联通WAP设置        中国联通因特网设置   
    //3gwap                 3gnet                uniwap            uninet   
       
       
    /**  
     * 3G wap 中国联通3gwap APN   
     */  
    public static String GWAP_3 = "3gwap";
       
    /**  
     * 3G net 中国联通3gnet APN   
     */  
    public static String GNET_3="3gnet";
       
    /**  
     * uni wap 中国联通uni wap APN   
     */  
    public static String UNIWAP="uniwap";
    /**  
     * uni net 中国联通uni net APN   
     */  
    public static String UNINET="uninet";
    
	/**  
	 * 电信APN列表  
	 * @author wudongdong  
	 *  
	 */  
	 public static final String CTWAP="ctwap";
	 public static final String CTNET="ctnet";


	private static Hashtable<String,String> networkTable = new Hashtable<String,String>();
	public static void switchNetType(Context context, String from, String to){}
	
	
	/**
	 * 判断网络是否可用
	 * @param context
	 * @return true 网络可用, false 网络不可用
	 */
	public static boolean isNetWorkAvailable(Context context) {
		netAvaliable = false ;
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager!=null){
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo!=null){
				DetailedState detailedState = networkInfo.getDetailedState();
				String detailedName = detailedState.name();
				if(detailedName==null){
					detailedName = "";
				}
				networkTable.put("detailedName", detailedName);
				String extraInfo = networkInfo.getExtraInfo();
				if(extraInfo==null){
					extraInfo = "";
				}
				networkTable.put("extraInfo", extraInfo);
				
				String reason = networkInfo.getReason();
				if(reason==null){
					reason = "";
				}
				networkTable.put("reason", reason);
				NetworkInfo.State state = networkInfo.getState();
				String stateName = "";
				if(state!=null && state.name()!=null){
					stateName=state.name();
				}
				if("CONNECTED".equalsIgnoreCase(stateName)){
					netAvaliable = true ;
				}
				networkTable.put("stateName", stateName);
				int subType = networkInfo.getSubtype();
				networkTable.put("subType", subType+"");
				String subtypeName = networkInfo.getSubtypeName();
				if(subtypeName==null){
					subtypeName = "";
				}
				networkTable.put("subtypeName", subtypeName);
				int type = networkInfo.getType();
				String typeName = networkInfo.getTypeName();
				if(typeName==null){
					typeName = "";
				}
				networkTable.put("typeName", typeName);
				Log.d(LOG_TAG,"当前网络状态："+getLogString());
			}
		}
		return netAvaliable;
	}
	public static String getLogString(){
		StringBuilder sb = new StringBuilder();
		sb.append("detailedName="+networkTable.get("detailedName"));
		sb.append(" extraInfo="+networkTable.get("extraInfo"));
		sb.append(" reason="+networkTable.get("reason"));
		sb.append(" stateName="+networkTable.get("stateName"));
		sb.append(" subtypeName="+networkTable.get("subtypeName"));
		sb.append(" typeName="+networkTable.get("typeName"));
		return sb.toString();
	}
	public static String apnType(){
		String apn = "" ;
		String netType = networkTable.get("typeName");
		if("CONNECTED".equals(networkTable.get("stateName"))){
			if("wifi".equalsIgnoreCase(netType)){
				apn = netType;
			}else if("mobile".equalsIgnoreCase(netType)){
				netType = networkTable.get("extraInfo").toLowerCase();    
	    		apn = matchAPN(netType);
			}else{
				apn = netType ;
				Log.e(LOG_TAG,"UNKNOW networkType:"+getLogString());
			}
		}else{
			
		}
		return apn ;
	}
	 public static String matchAPN(String currentName) {
	        if("".equals(currentName) || null==currentName){   
	            return "";   
	        }   
	        currentName = currentName.toLowerCase();   
	        if(currentName.startsWith(CMNET))   
	            return CMNET;   
	        else if(currentName.startsWith(CMWAP))   
	            return CMWAP;   
	        else if(currentName.startsWith(GNET_3))   
	            return GNET_3;   
	        else if(currentName.startsWith(GWAP_3))   
	            return GWAP_3;   
	        else if(currentName.startsWith(UNINET))   
	            return UNINET;   
	        else if(currentName.startsWith(UNIWAP))   
	            return UNIWAP;
	        else if(currentName.startsWith(CTWAP))   
	            return CTWAP;
	        else if(currentName.startsWith(CTNET))   
	            return CTNET;
	        else if(currentName.startsWith("default"))   
	            return "default";   
	        else{ 
	        	Log.d(LOG_TAG,"接入点类型未识别,apn="+currentName+",接入点信息="+getLogString());
	        	return currentName;   
	        }
	      
	    }   
}
