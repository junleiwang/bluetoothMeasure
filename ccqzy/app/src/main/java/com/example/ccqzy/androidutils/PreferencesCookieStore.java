package com.example.ccqzy.androidutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.ccqzy.QzyApplication;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A CookieStore impl, it's save cookie to SharedPreferences.
 * 缓存session值   告知服务器是否cookie，然后是否进行对cookie进行操作
 * @author michael yang
 */
public class PreferencesCookieStore implements CookieStore {

    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final ConcurrentHashMap<String, Cookie> cookies;
    private final SharedPreferences cookiePrefs;
    
    public static CookieStore cookieStore=null;

    /**
     * 构建一个持久的cookie  Store。
     */
    public PreferencesCookieStore(Context context) {
    	
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        cookies = new ConcurrentHashMap<String, Cookie>();

        // 任何先前存储的cookie都加载到store
        String storedCookieNames = cookiePrefs.getString(COOKIE_NAME_STORE, null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        cookies.put(name, decodedCookie);
                    }
                }
            }
            // 清除过期的cookie
            clearExpired(new Date());
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
    	
        String name = cookie.getName();
        // 如果过期了，将cookie保存到本地存储或删除,
        if (!cookie.isExpired(new Date())) {
        	LogUtils.d("add cookie： "+name);
            cookies.put(name, cookie);
        } else {
        	LogUtils.d("remove cookie： "+name);
            cookies.remove(name);
        }

        // 保存cookie持久性存储
        SharedPreferences.Editor editor = cookiePrefs.edit();
        editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        editor.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        editor.commit();
    }

    @Override
    public void clear() {
        // 清除持续性的cookies
        SharedPreferences.Editor editor = cookiePrefs.edit();
        for (String name : cookies.keySet()) {
            editor.remove(COOKIE_NAME_PREFIX + name);
            DataCleanManager.cleanApplicationData(QzyApplication.getAppContext(), name);
        }
        editor.remove(COOKIE_NAME_STORE);
        editor.commit();
        // 清除本地cookies
        cookies.clear();
    	LogUtils.d("parent Clear ");
    }

    @Override
    public boolean clearExpired(Date date) {
    	LogUtils.d("clearExpired ");
        boolean clearedAny = false;
        SharedPreferences.Editor editor = cookiePrefs.edit();

        for (ConcurrentHashMap.Entry<String, Cookie> entry : cookies.entrySet()) {
            String name = entry.getKey();
            Cookie cookie = entry.getValue();
            if (cookie.getExpiryDate() == null || cookie.isExpired(date)) {
                // 清除cookies中的name
                cookies.remove(name);

                // 清除cookies中的持久性数据 Clear cookies from persistent store
                editor.remove(COOKIE_NAME_PREFIX + name);

                // 清除到最后一个
                clearedAny = true;
            }
        }

        // 更新持久性数据那么
        if (clearedAny) {
            editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        }
        editor.commit();
        return clearedAny;
    }

    @Override
    public List<Cookie> getCookies() {
        return new ArrayList<Cookie>(cookies.values());
    }
//    @Override
//    public List<Cookie> getCookies() {
//    	return httpC;
//    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }


    protected String encodeCookie(SerializableCookie cookie) {
    	
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (Throwable e) {
            return null;
        }
        return byteArrayToHexString(os.toByteArray());
    }
    /**
     * 解析Cookie
     * @param cookieStr
     * @return
     */
    protected Cookie decodeCookie(String cookieStr) {
        byte[] bytes = hexStringToByteArray(cookieStr);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            cookie = ((SerializableCookie) ois.readObject()).getCookie();
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
        }
        return cookie;
    }

    /**
     * 字节数组的转换
     * @param b
     * @return
     */
    protected String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public class SerializableCookie implements Serializable {
        private static final long serialVersionUID = 6374381828722046732L;

        private transient final Cookie cookie;
        private transient BasicClientCookie clientCookie;

        public SerializableCookie(Cookie cookie) {
            this.cookie = cookie;
        }

        public Cookie getCookie() {
            Cookie bestCookie = cookie;
            if (clientCookie != null) {
                bestCookie = clientCookie;
            }
            return bestCookie;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(cookie.getName());
            out.writeObject(cookie.getValue());
            out.writeObject(cookie.getComment());
            out.writeObject(cookie.getDomain());
            out.writeObject(cookie.getExpiryDate());
            out.writeObject(cookie.getPath());
            out.writeInt(cookie.getVersion());
            out.writeBoolean(cookie.isSecure());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            String name = (String) in.readObject();
            String value = (String) in.readObject();
            clientCookie = new BasicClientCookie(name, value);
            clientCookie.setComment((String) in.readObject());
            clientCookie.setDomain((String) in.readObject());
            clientCookie.setExpiryDate((Date) in.readObject());
            clientCookie.setPath((String) in.readObject());
            clientCookie.setVersion(in.readInt());
            clientCookie.setSecure(in.readBoolean());
        }
    }
}