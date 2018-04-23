package com.taihua.th_radioplayer.player;

import com.taihua.th_radioplayer.download.DownloadState;

public class RadioItem {
	
	private int mChannelID = -1;
	private int mRadioID = -1;
	private DownloadState mDownloadStatus;
	private String mRadioName = null;
	private String mRadioSinger = null;
	private String mRadioAlbum = null;
	private String mDownloadUrl = null;
	private String mSavePath = null;
	
	public RadioItem() {
		
	}
	
	public RadioItem(int channelID, int radioID, String name, String singer, String album, String url) {
		
		mChannelID = channelID;
		mRadioID = radioID;
		mDownloadStatus = DownloadState.STOPPED;
		mRadioName = name;
		mRadioSinger = singer;
		mRadioAlbum = album;
		mDownloadUrl = url;
	}
	
	public void setRadioID(int radioID) {
		mRadioID = radioID;
	}

	public int getRadioID() {
		return mRadioID;
	}
	
	public int getChannelID() {
		return mChannelID;
	}
	
	public void setChannelID(int channelID) {
		mChannelID = channelID;
	}
	
	public void setRadioName(String radioName) {
		mRadioName = radioName;
	}
	
	public String getRadioName() {
		return mRadioName;
	}
	
	public void setRadioSinger(String radioSinger) {
		mRadioSinger = radioSinger;
	}
	
	public String getRadioSinger() {
		return mRadioSinger;
	}
	
	public void setRadioAlbum(String radioAlbum) {
		mRadioAlbum = radioAlbum;
	}
	
	public String getRadioAlbum() {
		return mRadioAlbum;
	}
	
	public void setDowanloadUrl(String dowanloadUrl) {
		mDownloadUrl = dowanloadUrl;
	}
	
	public String getDowanloadUrl() {
		return mDownloadUrl;
	}
	
	public void setSavePath(String savePath) {
		mSavePath = savePath;
	}
	
	public String getSavePath() {
		return mSavePath;
	}
	
	public String getPlayPath() {
		if(mDownloadStatus == DownloadState.FINISHED)
			return mSavePath;
		return mDownloadUrl;
	}

	public void setDowanloadStatus(DownloadState status) {
		// TODO Auto-generated method stub
		mDownloadStatus = status;
	}

	public DownloadState getDowanloadStatus() {
		// TODO Auto-generated method stub
		return mDownloadStatus;
	}

	@Override
	public String toString() {
		return "RadioItem ["
				+ " mRadioID=" + mRadioID 
				+ ", mRadioName=" + mRadioName
				+ ", mRadioSinger=" + mRadioSinger
				+ ", mRadioAlbum=" + mRadioAlbum
				+ ", mDownloadUrl=" + mDownloadUrl
                + ", mDownloadStatus=" + mDownloadStatus
				+ "]";
	}
}