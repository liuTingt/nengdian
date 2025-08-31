package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

public class WechatLoginRep {
    @JSONField(name = "openid")
    private String openid;
    @JSONField(name = "session_key")
    private String session_key;
    @JSONField(name = "unionid")
    private String unionid;
    @JSONField(name = "errcode")
    private Integer errcode;
    @JSONField(name = "errmsg")
    private String errmsg;

    public boolean isSuccess() {
        return errcode == null;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}
