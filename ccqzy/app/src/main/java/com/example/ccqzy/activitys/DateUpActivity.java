package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ccqzy.ChangChunActivity;
import com.example.ccqzy.GlobalContext;
import com.example.ccqzy.QzyApplication;
import com.example.ccqzy.R;
import com.example.ccqzy.adapters.UpdateListAdapter;
import com.example.ccqzy.androidutils.JsonUtil;
import com.example.ccqzy.androidutils.NetConnectManager;
import com.example.ccqzy.beans.CarbodyCalculatedDatas;
import com.example.ccqzy.beans.CarbodyMeasureInfos;
import com.example.ccqzy.beans.CarbodyMeasuredDatas;
import com.example.ccqzy.beans.ObjectAll;
import com.example.ccqzy.daos.ServiceCallBack;
import com.example.ccqzy.services.UserService;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.ccqzy.QzyApplication.cimcs;

public class DateUpActivity extends Activity implements View.OnClickListener {

    private TextView tvDateUp;//上传按钮
    private TextView tvRequest;//请求按钮
    private ListView lvPointList;//列表集合
    private RelativeLayout rlNoDate;
    private RelativeLayout rlUp;

    //文件详情adapter
    UpdateListAdapter exportAdapter;
    //文件列表集合
    public List<CarbodyMeasureInfos> all = new ArrayList<>();

    UserService service;
    private String url = "";//请求口令的url地址
    private String uploadUrl = "";//上传的url地址
    private String requestUrl = "";//请求站点的url地址

    //上传参数的集合
    //转换后点集合
    List<CarbodyCalculatedDatas> CarbodyCalculatedDatas = new ArrayList<>();
    //原始坐标点集合
    List<CarbodyMeasuredDatas> CarbodyMeasureData = new ArrayList<>();
    //文件集合
    List<CarbodyMeasureInfos> CarbodyMeasureInfos = new ArrayList<>();

    private String mac;//手机mac地址
    private String token;//用户口令

    //测试数据库各个点的坐标
    DbManager dbManager, dbManager1;
    //原始弧度坐标数据库
    DbManager ratDbManager;
    UUID uuid = UUID.randomUUID();

    //接收从数据库里获取的点坐标
    List<CarbodyCalculatedDatas> allPoint = null;

    //列表集合
    List<String> name = new ArrayList<>();
    //文件名称
    private String fileName;

    public static DateUpActivity instance;

    public DateUpActivity() {
        instance = DateUpActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_up);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //初始化标题头
        initTitle();

        initView();

        service = new UserService();
        url = GlobalContext.getUrl(R.string.url_login);
        uploadUrl = GlobalContext.getUrl(R.string.url_my_upload);
        requestUrl = GlobalContext.getUrl(R.string.url_request_point);
        mac = getIpAndMacAddress();
        //获取上传的口令
        // getToken();

        dbManager = x.getDb(((QzyApplication) getApplication()).getCarbodyMeasureConfig());
        dbManager1 = x.getDb(((QzyApplication) getApplication()).getCarbodyCalculatedDatasConfig());
        ratDbManager = x.getDb(((QzyApplication) getApplication()).getRadianConfig());

        query();
        getToken();
    }

    //初始化控件
    private void initView() {
        tvDateUp = findView(R.id.tv_date_up);
        //tvRequest = findView(R.id.tv_request);
        lvPointList = findView(R.id.lv_dateup_list);
        //tvRequest.setOnClickListener(this);
        tvDateUp.setOnClickListener(this);

        rlNoDate = findView(R.id.rl_no_date);
        rlUp = findView(R.id.rl_up);

        //点击条目查看详情
        lvPointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemId = all.get(position).getId();
                LogUtils.d("查看详情的单条目id==================="+itemId);
                Intent intent = new Intent(DateUpActivity.this, FileInfosActivity.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });
    }

    /*
* 获取mac地址
* */
    private String getIpAndMacAddress() {
        String ip = "";
        boolean isBreak = false;
        String name = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                name = intf.getName();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress().toString();
                        isBreak = true;
                        break;
                    }
                }
                if (isBreak) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mac = "";
        if (!TextUtils.isEmpty(name)) {
            try {
                byte[] address = NetworkInterface.getByName(name)
                        .getHardwareAddress();
                if (address != null) {
                    mac = byte2hex(address, address.length);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(mac) && !TextUtils.isEmpty(ip)) {
            // return ip + "_" + mac;
            return mac;
        }
        return "";
    }

    private static String byte2hex(byte[] b, int length) {
        StringBuffer hs = new StringBuffer(length);
        String stmp = "";
        int len = length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
            if (n != len - 1) {
                hs.append(":");
            }
        }
        return String.valueOf(hs);
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateUpActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("数据上传");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date_up:
                //判断当前是否有网络,上传数据
                if (NetConnectManager.isNetWorkAvailable(QzyApplication.getAppContext())) {
                    //上传所有数据
                    getAllDates();

                } else {
                    ChangChunActivity.instance.showTip("网络不可用,请检测网络连接");
                }
                break;

            default:
                break;
        }
    }

    /*
    * 请求站点坐标方法
    * */
    private void requestDates() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("MAC", mac);

        service.requesDates(HttpRequest.HttpMethod.POST, requestUrl, requestMap, token, new ServiceCallBack<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailed(int type, String showMessage, String detailMessage) {

                ChangChunActivity.instance.showTip("请求失败");
            }

            @Override
            public void onSuccess(String s) {
                ChangChunActivity.instance.showTip("请求成功");
            }

            @Override
            public void onLoading(long current, long total) {

            }

            @Override
            public void onCancel() {

            }
        });

    }


    //上传数据的方法
    private void uploadDates(final String itemid) {

        String t = JsonUtil.list2json(CarbodyMeasureInfos);
        String carbodyMeasureInfos = t.length() > 1 ? t.substring(0, t.length()) : "";
        LogUtils.d(mac + "==" + carbodyMeasureInfos);


        ObjectAll objectAll = new ObjectAll();
        objectAll.setMAC(mac);
        objectAll.setCarbodyMeasureInfos(carbodyMeasureInfos);

        LogUtils.d("objectall==" + objectAll.toString());


        //测试的post请求
        service.uploadDatesTest(uploadUrl, mac, objectAll, token, new ServiceCallBack<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailed(int type, String showMessage, String detailMessage) {
                LogUtils.d("数据上传失败");
                ChangChunActivity.instance.showTip("数据上传失败,请重新上传");
            }

            @Override
            public void onSuccess(String s) {
                ChangChunActivity.instance.showTip("数据上传成功");
                updateFlog(itemid);

            }

            @Override
            public void onLoading(long current, long total) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    //更新列表方法
    public void getExportDate(List<CarbodyMeasureInfos> name) {
        exportAdapter = new UpdateListAdapter(name, this);
        lvPointList.setAdapter(exportAdapter);
        exportAdapter.notifyDataSetChanged();
    }

    /*
   * 重新获取令牌
   * */
    private void getToken() {
        Map<String, String> loginMap = new HashMap<>();

        loginMap.put("TenancyName", "Demo");
        loginMap.put("UsernameOrEmailAddress", "user1");
        loginMap.put("Password", "123456");
        loginMap.put("MAC", mac);


        service.loginNet(HttpRequest.HttpMethod.POST, url, loginMap, new ServiceCallBack<String>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailed(int type, String showMessage, String detailMessage) {

                LogUtils.d("获取新令牌失败");
            }

            @Override
            public void onSuccess(String result) {

                token = "Bearer" + " " + result;
                LogUtils.d("获取新令牌成功" + result.toString());

            }

            @Override
            public void onLoading(long current, long total) {

            }

            @Override
            public void onCancel() {

            }
        });

    }


    //添加弧度坐标数据的方法
    private void addDates(CarbodyMeasuredDatas ccld) {
        CarbodyMeasureData.clear();

        CarbodyMeasuredDatas cmd = new CarbodyMeasuredDatas();
        cmd.setId(ccld.getId());
        cmd.setMeasureInfoId(ccld.getMeasureInfoId());
        cmd.setMeasurePointId(ccld.getMeasurePointId());
        cmd.setMeasurePointName(ccld.getMeasurePointName());
        cmd.setSortOrder(ccld.getSortOrder());
        cmd.setHorizontalAngle(ccld.getHorizontalAngle());
        cmd.setRightAngles(ccld.getRightAngles());
        cmd.setSlantDistance(ccld.getSlantDistance());
        cmd.setCreationTime(ccld.getCreationTime());

        CarbodyMeasureData.add(cmd);

    }

    //临时添加数据的方法
    private void addUpdateMessage(CarbodyMeasureInfos cm, String itemid) {
        LogUtils.d("两个id的比较"+cm.getId()+"是否等于"+itemid);
        CarbodyMeasureInfos.clear();

        queryPoint(itemid);
        ratQueryPoint(itemid);

        CarbodyMeasureInfos cmi = new CarbodyMeasureInfos();
        cmi.setId(cm.getId());
        cmi.setName(cm.getName());
        cmi.setComponent(cm.getComponent());
        cmi.setCarType(cm.getCarType());
        cmi.setCarModel(cm.getCarModel());
        cmi.setSteelNo(cm.getSteelNo());
        cmi.setTrainNo(cm.getTrainNo());
        cmi.setObserveNo(cm.getObserveNo());
        cmi.setPosition(cm.getPosition());
        cmi.setX(String.valueOf(cimcs.X));
        cmi.setY(String.valueOf(cimcs.Y));
        cmi.setH(String.valueOf(cimcs.Z));
        cmi.setCreationTime(cm.getCreationTime());
        cmi.setStandpointId(cm.getStandpointId());
        cmi.setCarbodyCalculatedDatas(CarbodyCalculatedDatas);
        cmi.setCarbodyMeasuredDatas(CarbodyMeasureData);

        CarbodyMeasureInfos.add(cmi);


        LogUtils.d("添加数据为=="+CarbodyMeasureInfos.size()+"里面的数据为===========" + CarbodyMeasureInfos.toString());

        uploadDates(itemid);
    }

    /*
    * 初始化控件的封装方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }

    //
    public void updateFlog(String id) {
        try {
            CarbodyMeasureInfos user = new CarbodyMeasureInfos();
            user.setId(id);
            user.setUped(true);
            dbManager.update(user, "uped");
            LogUtils.d("模板数据更新成功finished=" + user.isUped() + "//id==" + user.getId());

            //更新数据库
            query();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //获取数据库的CarbodymeasureInfos的信息
    public void query() {
        name.clear();
        try {
            Selector<CarbodyMeasureInfos> selector = dbManager.selector(CarbodyMeasureInfos.class);
            all = selector.findAll();
            LogUtils.d("详情的所有数据db_all==" + all);
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (all !=null){
        if (all.size() > 0) {
            getExportDate(all);
            rlNoDate.setVisibility(View.GONE);
            rlUp.setVisibility(View.VISIBLE);

        } else {
            rlNoDate.setVisibility(View.VISIBLE);
            rlUp.setVisibility(View.GONE);
        }
    }else {
            rlNoDate.setVisibility(View.VISIBLE);
            rlUp.setVisibility(View.GONE);
        }
    }

    //从数据库中获取数据
    public void queryPoint(String itemId) {
        try {
            Selector<CarbodyCalculatedDatas> selector = dbManager1.selector(CarbodyCalculatedDatas.class);
            allPoint = selector.findAll();
            if (allPoint != null) {
                CarbodyCalculatedDatas.clear();
                LogUtils.d("详情的点坐标数据库db_all==" + allPoint + allPoint.size());
                for (CarbodyCalculatedDatas user : allPoint) {
                    //测量的MeasureInfoId与文件id相同,并且isOver为true(表示已经测量过的数据)
                    if (user.getMeasureInfoId().equals(itemId) && user.isIsOver()) {
                        CarbodyCalculatedDatas.add(user);
                        //addDates(user);
                    }
                }

            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //从数据库中获取数据
    public void ratQueryPoint(String itemId) {
        try {
            Selector<CarbodyMeasuredDatas> selector = ratDbManager.selector(CarbodyMeasuredDatas.class);
            List<CarbodyMeasuredDatas> ratAllPoint = selector.findAll();
            if (ratAllPoint != null) {
                LogUtils.d("详情的点坐标数据库1231341312321312312312db_all==" + ratAllPoint + ratAllPoint.size());
                for (CarbodyMeasuredDatas user : ratAllPoint) {
                    //测量的MeasureInfoId与文件id相同,并且isOver为true(表示已经测量过的数据)
                    if (user.getMeasureInfoId().equals(itemId)) {
                        addDates(user);
                    }
                }

            } else {
                LogUtils.d("没有数据");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //上传单条目数据过程添加数据的方法
    public void getItemDates(String flogId) {
        if (all != null) {
            for (CarbodyMeasureInfos user1 : all) {
                if (user1.getId().equals(flogId)) {
                    addUpdateMessage(user1, flogId);
                }
            }
        } else {
            LogUtils.d("文件信息为空");
        }
    }

    //上传所有数据过程添加数据的方法
    public void getAllDates() {
        if (all != null) {
            for (CarbodyMeasureInfos user1 : all) {
                addUpdateMessage(user1, user1.getId());
            }
        } else {
            LogUtils.d("文件信息为空");
        }
    }

    //从本地数据库删除文件信息
    public void deleteDb1(String id) {
        //通过自己构建条件来删除
        try {
            dbManager.delete(CarbodyMeasureInfos.class, WhereBuilder.b("id", "=", id));
            LogUtils.d("删除成功,刷新成功");
            //参数解析 第一个参数是列名 第二个参数是条件= != > <等等条件，第三个参数为传递的值
            //如果条件参数不止一个的话，我们还可以使用.and("id", "=", id)方法
            //同理还有.or("id", "=", id)方法
            query();
        } catch (DbException e) {
        }
    }

}
