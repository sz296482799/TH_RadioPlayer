package com.taihua.th_radioplayer.domain;

public class BaseDataDataCarouselIB {

	private int pks_id;
	private String pks_name;
	private int pks_ver;

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

	@Override
	public String toString() {
		return "BaseDataCarouselInputBean [pks_id=" + pks_id + ", pks_name="
				+ pks_name + ", pks_ver=" + pks_ver + "]";
	}
	
	

}
