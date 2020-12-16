package cn.las.bean.entity;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private int code;

    private String message;

    private Map<String, Object> datas;

    public Message() {
        datas = new HashMap<String, Object>();
    }

    public Message(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

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

    public void putData(String key, Object value) {
        datas.put(key, value);
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, Object> datas) {
        this.datas = datas;
    }
}
