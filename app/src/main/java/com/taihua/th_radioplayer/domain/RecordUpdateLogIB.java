package com.taihua.th_radioplayer.domain;

public class RecordUpdateLogIB {
	private String update_id;
	private String hardware_num;
	private int channel_id;
	private int total_num;
	private int real_num;
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
	public int getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	public int getTotal_num() {
		return total_num;
	}
	public void setTotal_num(int total_num) {
		this.total_num = total_num;
	}
	public int getReal_num() {
		return real_num;
	}
	public void setReal_num(int real_num) {
		this.real_num = real_num;
	}
	public long getLog_add_time() {
		return log_add_time;
	}
	public void setLog_add_time(long log_add_time) {
		this.log_add_time = log_add_time;
	}
	@Override
	public String toString() {
		return "RecordUpdateLogInputBean [update_id=" + update_id
				+ ", hardware_num=" + hardware_num + ", channel_id="
				+ channel_id + ", total_num=" + total_num + ", real_num="
				+ real_num + ", log_add_time=" + log_add_time + "]";
	}

	
}
