package com.taihua.th_radioplayer.player;

import com.taihua.th_radioplayer.manager.RadioManager;

public class RadioList {
	
	private RadioPacket mCurPacket = null;
	
	public RadioList() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void clear() {
		mCurPacket = null;
	}
	
	public synchronized int size() {
		if(mCurPacket != null)
			return mCurPacket.size();
		return 0;
	}

    public RadioItem getRadio() {
        if(mCurPacket != null)
            return mCurPacket.getRadio();
        return null;
    }

	public synchronized String current() {
		if(mCurPacket != null)
			return mCurPacket.current();
		return null;
	}

	public synchronized String next() {
		if(mCurPacket != null)
			return mCurPacket.next();
		return null;
	}

	public synchronized String prev() {
		if(mCurPacket != null)
			return mCurPacket.prev();
		return null;
	}
	
	public synchronized boolean isEnd() {
		if(mCurPacket != null)
			return mCurPacket.isEnd();
		return true;
	}
	
	public synchronized String set(int index) {
		if(mCurPacket != null)
			return mCurPacket.set(index);
		return null;
	}
	
	public synchronized String setPlayID(int radioID) {
		if(mCurPacket != null)
			return mCurPacket.setPlayID(radioID);
		return null;
	}
	
	public RadioPacket getPacket(int packetID) {
		RadioPacket packet = null;
		
		if(packetID == -1)
			packet = mCurPacket;
		else {
			packet = RadioManager.getInstance().getPacketByID(packetID);
		}
		return packet;
	}

	public synchronized boolean switchPacket(int packetID) {
		mCurPacket = getPacket(packetID);
		return mCurPacket != null;
	}
	
	public synchronized boolean switchChannel(int channelID) {
		if(mCurPacket != null) {
			return mCurPacket.switchChannel(channelID);
		}
		return false;
	}

	public synchronized boolean switchRadio(int radioID) {
        if(mCurPacket != null) {
            return mCurPacket.switchRadio(radioID);
        }
		return false;
	}
}
