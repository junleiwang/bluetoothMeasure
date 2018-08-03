package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/1/24.
 * 登录返回的信息
 *  "targetUrl": null,
 "success": true,
 "error": null,
 "unAuthorizedRequest": false

 */

public class LoginMessage {

    private String result;
    private String targetUrl;
    private String error;
    boolean success;
    boolean unAuthorizedRequest;

    public LoginMessage() {
    }

    public LoginMessage(String result, String targetUrl, String error, boolean success,
                        boolean unAuthorizedRequest) {
        this.result = result;
        this.targetUrl = targetUrl;
        this.error = error;
        this.success = success;
        this.unAuthorizedRequest = unAuthorizedRequest;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isUnAuthorizedRequest() {
        return unAuthorizedRequest;
    }

    public void setUnAuthorizedRequest(boolean unAuthorizedRequest) {
        this.unAuthorizedRequest = unAuthorizedRequest;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "result='" + result + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", error='" + error + '\'' +
                ", success=" + success +
                ", unAuthorizedRequest=" + unAuthorizedRequest +
                '}';
    }
}
