package com.taihua.th_radioplayer.domain;

public class BaseDataDataChannelIB {
	private int channel_id;
	private int pks_id;
	private String channel_name;
	private int channel_ver;
	private String channel_pic;

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public int getPks_id() {
		return pks_id;
	}

	public void setPks_id(int pks_id) {
		this.pks_id = pks_id;
	}

	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	public int getChannel_ver() {
		return channel_ver;
	}

	public void setChannel_ver(int channel_ver) {
		this.channel_ver = channel_ver;
	}

	public String getChannel_pic() {
		return channel_pic;
	}

	public void setChannel_pic(String channel_pic) {
		this.channel_pic = channel_pic;
	}

	@Override
	public String toString() {
		return "BaseDataDataChannelInputBean [channel_id=" + channel_id
				+ ", pks_id=" + pks_id + ", channel_name=" + channel_name
				+ ", channel_ver=" + channel_ver + ", channel_pic="
				+ channel_pic + "]";
	}

}
