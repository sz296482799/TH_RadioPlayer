package com.taihua.th_radioplayer.player;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;

import android.util.Log;
import com.taihua.th_radioplayer.domain.ReturnMusicChannelMusicOB;
import com.taihua.th_radioplayer.domain.ReturnMusicChannelOB;
import com.taihua.th_radioplayer.download.DownloadState;
import com.taihua.th_radioplayer.utils.LogUtil;

public class RadioChannel {
	private int mChannelID = -1;
	private int mChannelVer = -1;
    private int downloadSuccNum = 0;
    private int downloadFailNum = 0;
	private String mChannelName = null;
	private String mChannelPic = null;
	private int mIndex = -1;	
	private ArrayList<RadioItem> mRadioList = null;
	private ArrayList<Integer> hasPacketList = null;
	
	@SuppressLint("UseSparseArrays")
	private void init() {
		mIndex = 0;
		mRadioList = new ArrayList<>();
		hasPacketList = new ArrayList<>();
	}
	
	public RadioChannel() {
		// TODO Auto-generated constructor stub
		init();
	}
	
	public RadioChannel(int channelID, int channelVer) {
		
		mChannelID = channelID;
		mChannelVer = channelVer;
		init();
	}

    public void setDownloadSuccNum(int downloadSuccNum) {
        this.downloadSuccNum = downloadSuccNum;
    }

    public int getDownloadSuccNum() {
        return downloadSuccNum;
    }

    public void setDownloadFailNum(int downloadFailNum) {
        this.downloadFailNum = downloadFailNum;
    }

    public int getDownloadFailNum() {
        return downloadFailNum;
    }

    public RadioItem getRadio(int radioID) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mRadioList.size(); i++) {
			if(mRadioList.get(i).getRadioID() == radioID) {
				return mRadioList.get(i);
			}
		}
		return null;
	}

	public int getRadioIndex(int radioID) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mRadioList.size(); i++) {
			if(mRadioList.get(i).getRadioID() == radioID) {
				return i;
			}
		}
		return -1;
	}

	public ArrayList<RadioItem> getRadios() {
		return mRadioList;
	}
	
	public void setRadios(ArrayList<RadioItem> radios) {
		mRadioList = radios;
	}

	public int getChannelID() {
		return mChannelID;
	}
	
	public void setChannelID(int channelID) {
		mChannelID = channelID;
	}
	
	public String getChannelName() {
		return mChannelName;
	}
	
	public void setChannelName(String channelName) {
		mChannelName = channelName;
	}
	
	public String getChannelPic() {
		return mChannelPic;
	}
	
	public void setChannelPic(String channelPic) {
		mChannelPic = channelPic;
	}
	
	public int getChannelVer() {
		return mChannelVer;
	}
	
	public void setChannelVer(int channelVer) {
		mChannelVer = channelVer;
	}
	
	public int size() {
		if(mRadioList != null)
			return mRadioList.size();
		return 0;
	}
	
	public String current() {

		if(mRadioList != null) {
			if(mIndex >= 0 && mIndex < mRadioList.size())
				return mRadioList.get(mIndex).getPlayPath();
		}
		return null;
	}

    public RadioItem getRadio() {

        if(mRadioList != null) {
            if(mIndex >= 0 && mIndex < mRadioList.size())
                return mRadioList.get(mIndex);
        }
        return null;
    }

	public String next() {

		if(mRadioList != null) {
			mIndex++;
			if(mIndex >= mRadioList.size()) mIndex = 0;
			if(mIndex >= 0 && mIndex < mRadioList.size())
				return mRadioList.get(mIndex).getPlayPath();
		}
		return null;
	}

	public String prev() {
		
		if(mRadioList != null) {
			mIndex--;
			if(mIndex < 0) mIndex = mRadioList.size() - 1;
			if(mIndex >= 0 && mIndex < mRadioList.size())
				return mRadioList.get(mIndex).getPlayPath();
		}
		return null;
	}
	
	public boolean isEnd() {
		if(mRadioList == null || mRadioList.size() == 0)
			return true;
		return mIndex == mRadioList.size() - 1;
	}
	
	public String set(int index) {

		if(mRadioList != null && mIndex >= 0 && mIndex < mRadioList.size()) {
			mIndex = index;
			return mRadioList.get(mIndex).getPlayPath();
		}
		return null;
	}
	
	public String setPlayID(int radioID) {
		if(mRadioList == null)
			return null;
		return set(getRadioIndex(radioID));
	}

	public boolean add(RadioItem radio) {

		if(mRadioList != null && radio != null) {
			mRadioList.add(radio);
			return true;
		}
		return false;
	}
	
	public ArrayList<Integer> getHasPacketList() {
		return hasPacketList;
	}

	// just use to RadioManager ! ! !
	public void update(ReturnMusicChannelOB c) {
		// TODO Auto-generated method stub
		if(c.getChannel_id() != mChannelID)
			return;
		
		mChannelVer = c.getChannel_ver();
		mRadioList.clear();
        downloadSuccNum = 0;
        downloadFailNum = 0;

		if(c.getMusics() == null) {
			return;
		}

		for(ReturnMusicChannelMusicOB m : c.getMusics()) {
			RadioItem item = new RadioItem();
			
			item.setChannelID(c.getChannel_id());
			item.setRadioID(m.getMusic_id());
			item.setRadioAlbum(m.getAlbum());
			item.setRadioName(m.getName());
			item.setRadioSinger(m.getSinger());
			item.setDowanloadUrl(m.getDownload_url());
			item.setDowanloadStatus(DownloadState.NONE);
			
			add(item);
		}
	}

    public void downloadOne(RadioItem item) {
	    if(item != null && mRadioList.indexOf(item) >= 0) {
            if(item.getDowanloadStatus() != DownloadState.ERROR)
                ++downloadSuccNum;
            else
                ++downloadFailNum;
        }
    }

	public boolean isFinish() {
	    /*
	    int count = 0;
	    for(RadioItem radio : mRadioList) {
	        if(radio.getDowanloadStatus().value() > DownloadState.STARTED.value()) {
	            count++;
            }
        }
        */
        if((downloadSuccNum + downloadFailNum) == mRadioList.size()) {
	        return true;
        }
	    return false;
    }

	@Override
	public String toString() {
		return "RadioChannel [mChannelID=" + mChannelID
				+ ", mChannelVer=" + mChannelVer 
				+ ", mIndex=" + mIndex
				+ ", mRadioList=" + mRadioList
				+ "]";
	}
}
