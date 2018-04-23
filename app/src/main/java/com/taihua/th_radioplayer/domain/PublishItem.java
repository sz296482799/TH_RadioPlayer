package com.taihua.th_radioplayer.domain;

public class PublishItem {
    private String option;
    private String data;
    private String token;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "{"
                + "option:" + option + ","
                + "data:" + data + ","
                + "token:" + token
                + "}";
    }
}
