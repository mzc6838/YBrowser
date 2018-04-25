package com.mzc6838.ybrowser;

import java.util.List;

/**
 * Created by mzc6838 on 2018/4/19.
 */

public class ErrorResponse {
    private int errCode;
    private String msg;
    private String body;


    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
