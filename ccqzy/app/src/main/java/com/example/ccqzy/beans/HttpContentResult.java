package com.example.ccqzy.beans;

public class HttpContentResult<T> {

	public static final int SUCCESS = 1;
	public static final int FAILED = 2;
	public static final int SESSION_TIMEOUT = 3;
	public static final int NODATA = 4;

	public static enum ResultCode {
		成功(1), 失败(2), session超时(3), 数据不存在(4);
		public int code;

		private ResultCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	// 服务器返回的结果状态码
	private int code;
	// 服务器返回的结果说明
	private String message;
	// 服务器返回的具体的数据对象
	private T data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
