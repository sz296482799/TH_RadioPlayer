package com.taihua.th_radioplayer.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class JasonUtils {
	
	/**
	 * jason字符�? 转换 数组对象
	 * @param jasonstr
	 * @param object
	 * @return
	 */
	public static <T> List<T> Jason2Array(String jasonstr, Class<T> object) {
		List<T> base = null;
		try {
			base = JSON.parseArray(jasonstr, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base;
	}
	/**
	 * jason字符�? 转换 对象
	 * @param jasonstr
	 * @param object
	 * @return
	 */
	public static <T> T Jason2Object(String jasonstr, Class<T> object) {
		T base = null;
		try {
			base = JSON.parseObject(jasonstr, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base;
	}
	
	/**
	 * jason字符�? 转换 对象
	 * @param jsonstr
	 * @param object
	 * @return
	 */
	public static <T> T Jason2Object(String jsonstr, TypeReference<T> type) {
		T base = null;
		try {
			base = JSON.parseObject(jsonstr, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return base;
	}
	
	/**
	 * 对象 转换 jason字符�?
	 * @param initclientBean
	 * @return
	 */
	public static  String object2JsonString(Object initclientBean) {
		String temp =null;
		try{
			temp =  JSON.toJSONString(initclientBean);
		}catch(Exception e){
			e.printStackTrace();
		}
		return temp;
	}

	public static <T> String List2JsonString(List<T> list, TurnJson<T> t) {
        JSONArray array = new JSONArray();

        if(list != null) {
            for (T t1 : list) {
                array.add(t.turn(t1));
            }
        }
        return array.toJSONString();
    }
}