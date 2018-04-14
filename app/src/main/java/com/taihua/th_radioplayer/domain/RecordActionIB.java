package com.taihua.th_radioplayer.domain;

public class RecordActionIB {
	
	private String hardware_num;
	private int action_id;
	private int action_type;
	private int source_id;
	private long log_add_time;
	
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
	public int getAction_type() {
		return action_type;
	}
	public void setAction_type(int action_type) {
		this.action_type = action_type;
	}
	public int getSource_id() {
		return source_id;
	}
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}
	public long getLog_add_time() {
		return log_add_time;
	}
	public void setLog_add_time(long log_add_time) {
		this.log_add_time = log_add_time;
	}
	@Override
	public String toString() {
		return "RecordActionInputBean [hardware_num=" + hardware_num
				+ ", action_id=" + action_id + ", action_type=" + action_type
				+ ", source_id=" + source_id + ", log_add_time=" + log_add_time
				+ "]";
	}
}
