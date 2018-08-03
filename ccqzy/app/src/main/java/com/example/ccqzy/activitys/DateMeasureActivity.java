package com.example.ccqzy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ccqzy.R;

import java.util.UUID;

public class DateMeasureActivity extends Activity implements View.OnClickListener {

    private TextView tvAutomaticMeasure;//自动测量
    private TextView tvManualMeasure;//学习测量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_measure);

        //设置导航栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //初始化标题头
        initTitle();

        initView();
    }

    /*
    * 初始化标题头
    * */
    private void initTitle() {
        TextView back = findView(R.id.tv_title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateMeasureActivity.this.finish();
            }
        });

        TextView title = findView(R.id.tv_title_name);
        title.setText("数据测量");
    }

    /*
    * 初始化view
    * */
    private void initView() {
        tvAutomaticMeasure = findView(R.id.tv_automatic_measure);
        tvManualMeasure = findView(R.id.tv_manual_measure);

        tvManualMeasure.setOnClickListener(this);
        tvAutomaticMeasure.setOnClickListener(this);
    }

    /*
    * 点击事件
    * */

    String id = UUID.randomUUID().toString();
    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.tv_automatic_measure:
                intent = new Intent(DateMeasureActivity.this, FileMessageActivity.class);
                intent.putExtra("type", "automatic");
                startActivity(intent);
                DateMeasureActivity.this.finish();
                break;

            case R.id.tv_manual_measure:
                intent = new Intent(DateMeasureActivity.this, AddPointActivity.class);
                intent.putExtra("itemId", id);
                startActivity(intent);
                DateMeasureActivity.this.finish();
                break;

            default:
                break;
        }
    }

    /*
    * 自定义初始化控件方法
    * */
    public <T> T findView(int view) {
        return (T) super.findViewById(view);
    }


}
