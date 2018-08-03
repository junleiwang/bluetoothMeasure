package com.example.ccqzy.androidutils;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ccqzy.R;


public class AlertDialogToast {
	Context context;
	android.app.AlertDialog alertDailog;

	TextView sure;
	TextView cansure;
	TextView dialogTitle;
	TextView dialogContent;
	LinearLayout view;

	public AlertDialogToast(Context context) {
		super();
		this.context = context;
		alertDailog = new android.app.AlertDialog.Builder(context).create();
		alertDailog.show();

		Window window = alertDailog.getWindow();
		window.setContentView(R.layout.alertdialog_layout);
		sure = (TextView) window.findViewById(R.id.alertdialog_sure);
		cansure = (TextView) window.findViewById(R.id.alertdialog_cansure);
		dialogTitle = (TextView) window.findViewById(R.id.dialog_title);
		dialogContent = (TextView) window.findViewById(R.id.dialog_content);
		view = (LinearLayout) window.findViewById(R.id.view);

	}

	/**
	 * 设置标题
	 */
	public void setTitle(String title) {
		dialogTitle.setText(title);
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		dialogContent.setText(content);

	}

	/**
	 * 设置按钮
	 */

	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		sure.setText(text);
		sure.setOnClickListener(listener);
	}

	public void setNegativeButton(String text,
			final View.OnClickListener listener) {
		cansure.setText(text);
		cansure.setOnClickListener(listener);

	}

	public void setPartSureButton(String text,
			final View.OnClickListener listener) {
		sure.setText(text);
		sure.setOnClickListener(listener);
		cansure.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
	}

	public void setPositiveGone() {
		cansure.setVisibility(View.GONE);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		alertDailog.dismiss();
	}


}
