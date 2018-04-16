package com.taihua.th_radioplayer.domain;

public class ReturnTimeOB {
    private int response_code;
    private long time;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ReturnTimeOB [response_code=" + response_code
                + ", time=" + time + "]";
    }
}
