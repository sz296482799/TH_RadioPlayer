package com.taihua.th_radioplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;
  
public class UsbUtils {

	public static List<String> getMountPathList() {
	    List<String> pathList = new ArrayList<String>();    
	    final String cmd = "cat /proc/mounts";    
	    Runtime run = Runtime.getRuntime();
	    try {    
	        Process p = run.exec(cmd);
	        BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());    
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));    
	   
	        String line;    
	        while ((line = bufferedReader.readLine()) != null) {

	            String[] temp = TextUtils.split(line, " " );
	            String result = temp[1];

	            File file = new File(result);    
	            if (file.isDirectory() && file.canRead() && file.canWrite()) {

	                pathList.add(result);    
	            }    
	   
	            if (p.waitFor() != 0 && p.exitValue() == 1) {
	                break;
	            }    
	        }    
	        bufferedReader.close();    
	        inputStream.close();    
	    } catch (Exception e) {
	        pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
	    }  
	    return pathList;    
	}
}