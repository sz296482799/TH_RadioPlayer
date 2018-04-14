package com.taihua.th_radioplayer.domain;

public class RecordUpdateActionIB {
	
	private String update_id;
	private String hardware_num;
	private int action_id;
	private long log_add_time;
	
	public String getUpdate_id() {
		return update_id;
	}
	public void setUpdate_id(String update_id) {
		this.update_id = update_id;
	}
	public String getHardware_num() {
		return hardware_num;
	}
	public void setHardware_num(String hardware_num) {
		this.hardware_num = hardware_num;
	}
	public int getAction_id() {
		return action_id;
	}
	public void setAction_id(int action_id) {
		this.action_id = action_id;
	}
	public long getLog_add_time() {
		return log_add_time;
	}
	public void setLog_add_time(long log_add_time) {
		this.log_add_time = log_add_time;
	}
	@Override
	public String toString() {
		return "RecordUpdateActionInputBean [update_id=" + update_id
				+ ", hardware_num=" + hardware_num + ", action_id=" + action_id
				+ ", log_add_time=" + log_add_time + "]";
	}

	
	
}
