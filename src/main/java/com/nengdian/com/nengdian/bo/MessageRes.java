package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.logging.log4j.util.Strings;

public class MessageRes {
    @JSONField(name = "msgid")
    private String msgid;
    @JSONField(name = "errcode")
    private String errcode;
    @JSONField(name = "errmsg")
    private String errmsg;

    public boolean isSuccess() {
        return Strings.isBlank(errcode) || "0".equals(errcode);
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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
