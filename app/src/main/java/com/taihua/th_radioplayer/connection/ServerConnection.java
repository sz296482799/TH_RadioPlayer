package com.taihua.th_radioplayer.connection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.taihua.th_radioplayer.domain.*;
import com.taihua.th_radioplayer.global.Config;
import com.taihua.th_radioplayer.utils.ConnectUtil;
import com.taihua.th_radioplayer.utils.JasonUtils;
import com.taihua.th_radioplayer.utils.LogUtil;
import com.taihua.th_radioplayer.utils.MD5Util;

public class ServerConnection {

	public static final String TAG = "SEVER_CONNECTION";
	private static final String URL_HTTP_HEAD = "http://";

	private static ServerConnection mInstance;
    private String mServerUrl = Config.SERVER;
    private String mClientSecret;

    private ServerConnection() {
		// TODO Auto-generated constructor stub
	}
	
	public static ServerConnection getInstance() {

		if (mInstance == null) {
			synchronized (ServerConnection.class) {
				if (mInstance == null) {
					mInstance = new ServerConnection();
				}
			}
		}
		return mInstance;
	}
	
	public void setServerUrl(String serverUrl) {
		mServerUrl = serverUrl;
	}
	
	private String getBaseUrl() {
		return URL_HTTP_HEAD + mServerUrl + "/taihua/index.php";
	}
	
	private String getParamString(String name, String value) {
		return name + "=" + value + "&";
	}
	
	private String getOptionUrl(String option, String id) {
        return getBaseUrl() + "?"
				+ getParamString("c", "api")
				+ getParamString("op", option)
				+ (id != null ? getParamString("id", id) : "");
	}

    private String getSecretOptionUrl(String option, String id) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String api_token = null;
        if(mClientSecret != null)
            api_token = MD5Util.MD5(option + format.format(new Date()) + mClientSecret);
        return getOptionUrl(option, id)
                + (api_token != null ? getParamString("api_token", api_token) : "");
    }
	
	private String getBaseDataUrl(String id) {
		return getSecretOptionUrl(Config.Option.get_base_data.getName(), id);
	}
	
	private String getMusicUrl(String id, int channel_id, int page, int pagesize) {
		String url = getSecretOptionUrl(Config.Option.get_music_data.getName(), id);
		return url
				+ getParamString("ch_id", "" + channel_id)
				+ getParamString("page", "" + page)
				+ getParamString("pagesize", "" + pagesize);
	}
	
	private String getServerDataUrl(String id) {
		return getSecretOptionUrl(Config.Option.get_set_data.getName(), id);
	}
	
	private String setClientInitUrl() {
		return getOptionUrl(Config.Option.set_client_init.getName(), null);
	}
	
	private String setBoxLogUrl(String id) {
		return getOptionUrl(Config.Option.set_box_log.getName(), id);
	}

	public void setClientSecret(String clientSecret) {
        mClientSecret = clientSecret;
    }
	
	public synchronized ReturnOB setClientInit(InitclientIB bean) {

		String sendStr  = JasonUtils.object2JsonString(bean);
		if(sendStr == null)
		    return null;

        LogUtil.d(TAG, "setClientInit URL:" + setClientInitUrl());
		LogUtil.d(TAG, "setClientInit sendStr:" + sendStr);
		String jsonStr = ConnectUtil.jsonPost(setClientInitUrl(), sendStr);
        LogUtil.d(TAG, "setClientInit jsonStr:" + jsonStr);
		if(jsonStr == null)
		    return null;

        ReturnOB info = JasonUtils.Jason2Object(jsonStr, ReturnOB.class);
        if(info == null || info.getResponse_code() == 0)
            return null;
		return info;
	}

    public synchronized ReturnOB setBoxLog(String id, RecordIB bean) {

        String sendStr  = JasonUtils.object2JsonString(bean);
        if(sendStr == null)
            return null;

        LogUtil.d(TAG, "setBoxLog Url:" + setBoxLogUrl(id));
        LogUtil.d(TAG, "setBoxLog sendStr:" + sendStr);
        String jsonStr = ConnectUtil.jsonPost(setBoxLogUrl(id), sendStr);
        if(jsonStr == null)
            return null;

        LogUtil.d(TAG, "setBoxLog jsonStr:" + jsonStr);
        ReturnOB info = JasonUtils.Jason2Object(jsonStr, ReturnOB.class);
        if(info == null || info.getResponse_code() == 0)
            return null;
        return info;
    }
	
	public synchronized BaseDataIB getBaseData(String id) {

		String jsonStr = ConnectUtil.getContent(getBaseDataUrl(id));
		if(jsonStr == null)
		    return null;

        LogUtil.d(TAG, "getBaseData:" + jsonStr);
		BaseDataIB baseDataIB = JasonUtils.Jason2Object(jsonStr, BaseDataIB.class);
		if(baseDataIB == null || baseDataIB.getResponse_code() == 0)
			return null;

		return baseDataIB;
	}
	
	public synchronized ReturnMusicOB getChannelAllMusic(String id, int channel_id) {
		int page_index = 1;
		int page_size = 50;

        LogUtil.d(TAG, "getChannelAllMusic channelID:" + channel_id);
		
		ReturnMusicOB returnM = null;
		ReturnMusicOB returnTemp = null;
		do {
            LogUtil.d(TAG, "getMusic URL:" + getMusicUrl(id, channel_id, page_index, page_size));
			String strTemp = ConnectUtil.getContent(getMusicUrl(id, channel_id, page_index++, page_size));
            LogUtil.d(TAG, "getChannelAllMusic:" + strTemp);
			returnTemp = JasonUtils.Jason2Object(strTemp, ReturnMusicOB.class);
			if(returnTemp == null || returnTemp.getResponse_code() == 0)
				break;
			
			if(returnM == null) 
				returnM = returnTemp;
			else if(returnTemp != null 
				&& returnTemp.getData() != null
                && returnTemp.getData().size() > 0
				&& returnTemp.getData().get(0) != null) {
				List<ReturnMusicChannelMusicOB> musics1 = returnM.getData().get(0).getMusics();
				List<ReturnMusicChannelMusicOB> musics2 = returnTemp.getData().get(0).getMusics();
				for(ReturnMusicChannelMusicOB m : musics2) {
					musics1.add(m);
				}
			}
		}while(returnTemp != null 
				&& returnTemp.getData() != null
                && returnTemp.getData().size() > 0
				&& returnTemp.getData().get(0) != null
				&& returnTemp.getData().get(0).getMusic_count() > page_size * page_index);
		return returnM;
	}
	
	public synchronized ReturnSetOB getServerData(String id) {

		String jsonStr = ConnectUtil.getContent(getServerDataUrl(id));
		if(jsonStr == null)
		    return null;

        LogUtil.d(TAG, "getServerData:" + jsonStr);
        ReturnSetOB setObj = JasonUtils.Jason2Object(jsonStr, ReturnSetOB.class);
        if(setObj == null || setObj.getResponse_code() == 0)
            return null;
        return setObj;
	}
}
