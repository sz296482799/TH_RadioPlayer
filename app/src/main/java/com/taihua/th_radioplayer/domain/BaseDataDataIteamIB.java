package com.taihua.th_radioplayer.domain;

import java.util.List;

public class BaseDataDataIteamIB {

	private int pks_id;
	private String pks_name;
	private int pks_ver;
	private List<BaseDataDataChannelIB> channel;

	public int getPks_id() {
		return pks_id;
	}

	public void setPks_id(int pks_id) {
		this.pks_id = pks_id;
	}

	public String getPks_name() {
		return pks_name;
	}

	public void setPks_name(String pks_name) {
		this.pks_name = pks_name;
	}

	public int getPks_ver() {
		return pks_ver;
	}

	public void setPks_ver(int pks_ver) {
		this.pks_ver = pks_ver;
	}

	public List<BaseDataDataChannelIB> getChannel() {
		return channel;
	}

	public void setChannel(List<BaseDataDataChannelIB> channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "BaseDataDataIteamInputBean [pks_id=" + pks_id + ", pks_name="
				+ pks_name + ", pks_ver=" + pks_ver + ", channel=" + channel
				+ "]";
	}

}
