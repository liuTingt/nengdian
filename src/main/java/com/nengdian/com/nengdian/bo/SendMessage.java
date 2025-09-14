package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

public class SendMessage {

    @JSONField(name = "template_id")
    private String template_id;

    @JSONField(name = "touser")
    private String touser;

    @JSONField(name = "data")
    private Map<String, MessageData> data;

    /**
     * 跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
     */
    @JSONField(name = "miniprogram_state")
    private String miniprogram_state;
    /**
     * 进入小程序查看”的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN
     */
    @JSONField(name = "lang")
    private String lang = "zh_CN";

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public Map<String, MessageData> getData() {
        return data;
    }

    public void setData(Map<String, MessageData> data) {
        this.data = data;
    }

    public String getMiniprogram_state() {
        return miniprogram_state;
    }

    public void setMiniprogram_state(String miniprogram_state) {
        this.miniprogram_state = miniprogram_state;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
