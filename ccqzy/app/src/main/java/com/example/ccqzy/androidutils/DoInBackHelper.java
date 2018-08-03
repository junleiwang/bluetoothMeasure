package com.example.ccqzy.androidutils;

import android.os.AsyncTask;

/**
 * 在新线程里处理一些费时操作，该操作返回boolean类型的值，通过该返回值来判断是否进行UI操作 用法:
 * 
 * <pre class="prettyprint">
 * DoInBackHelper.waitNoInput(new IDoInBackHelper() {
 * 	public void doInUiThread(Integer i) {
 * 		// do in ui thread
 * 		// ...
 * 	}
 * 
 * 	public Integer doInBack() {
 * 		try {
 * 			Thread.sleep(500);
 * 		} catch (InterruptedException e) {
 * 			e.printStackTrace();
 * 		}
 * 		return 1;
 * 	}
 * });
 * </pre>
 * 
 * @author mbb 2014年10月23日09:31:39
 */
public class DoInBackHelper {

	public static void waitNoInput(IDoInBackHelper iDoInBackHelper) {
		if (iDoInBackHelper != null) {
			new DoInBackAsychTask(iDoInBackHelper).execute();
		}
	}

	static class DoInBackAsychTask extends AsyncTask<Integer, Void, Integer> {

		IDoInBackHelper iDoInBackHelper;

		public DoInBackAsychTask(IDoInBackHelper iDoInBackHelper) {
			this.iDoInBackHelper = iDoInBackHelper;
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int res = 0;
			try {
				res = iDoInBackHelper.doInBack();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				iDoInBackHelper.doInUiThread(result);//ui更新回调
			}
		}
	}
}
