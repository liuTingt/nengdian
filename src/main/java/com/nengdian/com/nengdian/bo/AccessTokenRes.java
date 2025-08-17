package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

public class AccessTokenRes {
    @JSONField(name = "access_token")
    private String access_token;
    @JSONField(name = "expires_in")
    private String expires_in;
    @JSONField(name = "errcode")
    private String errcode;
    @JSONField(name = "errmsg")
    private String errmsg;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
