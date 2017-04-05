package com.syz.tool.doc;

public class CommentEntry {
    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{\"key\":\"" + this.key + "\",\"value\":\"" + this.value
                + "\"}";
    }
}