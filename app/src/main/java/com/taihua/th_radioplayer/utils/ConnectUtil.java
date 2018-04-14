package com.taihua.th_radioplayer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectUtil {

	public static final String TAG = ConnectUtil.class.getName();

	public static String getContent(String strURL) {

		HttpURLConnection conn = null;
		StringBuffer sb = null;
		InputStreamReader inputReader = null;
		BufferedReader reader = null;
		String str = null;
		try {
			URL url = new URL(strURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(10 * 1000);
			if (conn.getResponseCode() == 200) {
				sb = new StringBuffer();
				inputReader = new InputStreamReader(conn.getInputStream());
				reader = new BufferedReader(inputReader);
				while ((str = reader.readLine()) != null) {
					sb.append(str).append("\n");
				}
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

    public static String jsonPost(String strURL, String params) {  
        try {
            URL url = new URL(strURL);// 创建连接  
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("POST"); // 设置请求方式  
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格�??  
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发�?�数据的格式  
            connection.connect();  
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码  
            out.append(params);  
            out.flush();  
            out.close();  
  
            int code = connection.getResponseCode();  
            InputStream is = null;  
            if (code == 200) {  
                is = connection.getInputStream();  
            } else {  
                is = connection.getErrorStream();  
            }  
  
            // 读取响应  
            int length = (int) connection.getContentLength();
            if (length != -1) {  
                byte[] data = new byte[length];  
                byte[] temp = new byte[512];  
                int readLen = 0;  
                int destPos = 0;  
                while ((readLen = is.read(temp)) > 0) {  
                    System.arraycopy(temp, 0, data, destPos, readLen);  
                    destPos += readLen;  
                }  
                String result = new String(data, "UTF-8");
                return result;  
            }
        } catch (IOException e) {
        }  
        return "error"; // 自定义错误信�??  
    } 
}
