package com.taihua.th_radioplayer.utils;

import com.alibaba.fastjson.JSONObject;

public interface TurnJson<T> {
    JSONObject turn(T obj);
}
