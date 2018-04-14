package com.taihua.th_radioplayer.player;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import com.taihua.th_radioplayer.domain.BaseDataDataChannelIB;
import com.taihua.th_radioplayer.domain.BaseDataDataIteamIB;
import com.taihua.th_radioplayer.manager.RadioManager;

public class RadioPacket {
	
	private ArrayList<Integer> hasChannelList;
	private int mCurChannelID = -1;
	private int mPacketID;
	private int mPacketVer;
	RadioManager mCManager;
	
	@SuppressLint("UseSparseArrays")
	public RadioPacket(int packetID, int packetVer) {
		mPacketID = packetID;
		mPacketVer = packetVer;
		hasChannelList = new ArrayList<>();
		mCManager = RadioManager.getInstance();
	}
	
	private RadioChannel getChannel(int channelID) {
		if(channelID < 0)
			return null;
		return mCManager.getChannelByID(channelID);
	}
	
	private RadioChannel getCurChannel() {
		return getChannel(mCurChannelID);
	}
	
	public int size() {
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.size();
		return 0;
	}

	public String current() {
		
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.current();
		return null;
	}

	public RadioItem getRadio() {

        RadioChannel c = getCurChannel();
        if(c != null)
            return c.getRadio();
		return null;
	}

	public String next() {
		
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.next();
		return null;
	}

	public String prev() {
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.prev();
		return null;
	}
	
	public boolean isEnd() {
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.isEnd();
		return true;
	}
	
	public String set(int index) {
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.set(index);
		return null;
	}
	
	public String setPlayID(int radioID) {
		RadioChannel c = getCurChannel();
		if(c != null)
			return c.setPlayID(radioID);
		return null;
	}

	public boolean switchChannel(int channelID) {
		RadioChannel channel = getChannel(channelID);
		if(channel != null) {
			mCurChannelID = channelID;
			return true;
		}
		return false;
	}
	
	public int getPacketID() {
		return mPacketID;
	}
	
	public int getPacketVer() {
		return mPacketVer;
	}
	
	public ArrayList<Integer> getHasChannelList() {
		return hasChannelList;
	}

	public void update(BaseDataDataIteamIB p) {
		// TODO Auto-generated method stub
		if(p.getPks_id() != mPacketID)
			return;
		
		mPacketVer = p.getPks_ver();
		hasChannelList.clear();

		if(p.getChannel() != null) {
			for(BaseDataDataChannelIB c : p.getChannel()) {
				hasChannelList.add(c.getChannel_id());
			}
		}
	}
}
