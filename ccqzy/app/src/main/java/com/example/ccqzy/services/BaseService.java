package com.example.ccqzy.services;

import android.text.TextUtils;

import com.example.ccqzy.androidutils.JsonUtil;
import com.example.ccqzy.beans.HttpContentResult;
import com.example.ccqzy.beans.NotifyNewsInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lidroid.xutils.util.LogUtils;

import java.io.IOException;
import java.util.List;

public class BaseService {

	public int timeout = 60 * 1000;

	/**
	 * 解析异步请求返回的json字符串
	 * 
	 * @param json
	 *            请求结果
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> HttpContentResult<T> json2HttpContentResult(String json,
														   TypeReference typeReference) throws JsonProcessingException,
			IOException {
		HttpContentResult<T> result = null;
		if (TextUtils.isEmpty(json)) {
			LogUtils.e("登陆返回的json字符串为空");
			return null;
		}
		result = (HttpContentResult<T>) JsonUtil.node2pojo(JsonUtil.json2node(json), typeReference);
		return result;
	}

	public <T> List<T> json2HttpContentResult1(String json,
											   TypeReference typeReference) throws JsonProcessingException,
			IOException {
		HttpContentResult<T> result = null;
		List<NotifyNewsInfo> json2list = null;
		if (TextUtils.isEmpty(json)) {
			LogUtils.e("登陆返回的json字符串为空");
			return null;
		}
		try {
			json2list = JsonUtil.json2list(json, NotifyNewsInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// result = JsonUtil.node2pojo(JsonUtil.json2node(json), typeReference);
		return (List<T>) json2list;
	}
}
