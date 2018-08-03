package com.example.ccqzy.daos;

import com.example.ccqzy.beans.ObjectAll;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * http协议 dao层 返回String类型，不作处理返回到service层，所以命名曰baseDao
 * 数据请求类
 *
 * @author mbb
 */
public class BaseDaoNetTest {

    public synchronized void swap1(final String url, String mac, ObjectAll object, String token,
                                   final AjaxCallBack<String> callback) {

        //请求路径,请求头参数,
        RequestParams params = new RequestParams(url);
        params.addHeader("Content-Type", "application/json");
        params.addHeader("Authorization", token);

        LogUtils.d("提交的参数objcet>" + object.toString());

        //请求体数据
        params.setBodyContent(object.toString());

//        ObjectMapper mapper = new ObjectMapper();
    //        try {
    //            String json = mapper.writeValueAsString(object);
//            params.setBodyContent(json);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

        //设置变量
        // params.setBodyContent("{\"mac\":\"B4:CE:F6:B9:69:EE\",\"carbodyMeasureInfos\":[{\"carModel\":\"EC01\",\"carType\":\"CC\",\"carbodyCalculatedDatas\":[{\"IsInitail\":false, \"creationTime\":\"2018-01-01T00:00:00 \",\"h\":\"17587.237\",\"id\":\"22819b14-93f5-464e-83f5-11316199f19b\",\"measureInfoId\":\"358873d8-1a2f-4a5b-ad38-0efbe3f7c9cc\",\"measurePointId\":\"14eaf34e-61c4-4fbd-b167-923d67b6b919\",\"measurePointName\":\"BGz_RI10\",\"sortOrder\":\"1\",\"x\":\"17587.237\",\"y\":\"15587.237\"}],\"component\":\"CBSH\",\"creationTime\":\"2018-01-01T00:00:00\",\"h\":\"1228.971\",\"id\":\"358873d8-1a2f-4a5b-ad38-0efbe3f7c9cc\",\"name\":\"CRH3\",\"observeNo\":\"3\",\"position\":\"1\",\"standpointId\":\"56173f5a-3747-44ea-a36f-a4e6b7474c70\",\"steelNo\":\"003B\",\"trainNo\":\"1\",\"x\":\"1228.971\",\"y\":\"1228.971\"}]}");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String t) {

                LogUtils.d("http请求数据成功：\n 请求的地址：" + url + "\n返回的结果：" + t);
                callback.onSuccess(t);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                callback.onFailed("请求失败", "失败");
                LogUtils.d("http请求数据失败：\n 请求的地址：" + url);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                LogUtils.d("http请求结束：\n 请求的地址：" + url);
            }
        });
    }

}
