package com.taihua.th_radioplayer.domain;

public class ReturnSetDataBroadcastOB {
	private int br_id;
	private String br_file_name;
	private String br_file;
	private String br_date;
	private String br_time;
	private int br_duration;
	private int br_mode;
	private int br_repeat_num;

	public int getBr_id() {
		return br_id;
	}

	public void setBr_id(int br_id) {
		this.br_id = br_id;
	}

	public String getBr_file_name() {
		return br_file_name;
	}

	public void setBr_file_name(String br_file_name) {
		this.br_file_name = br_file_name;
	}

	public String getBr_file() {
		return br_file;
	}

	public void setBr_file(String br_file) {
		this.br_file = br_file;
	}

	public String getBr_date() {
		return br_date;
	}

	public void setBr_date(String br_date) {
		this.br_date = br_date;
	}

	public String getBr_time() {
		return br_time;
	}

	public void setBr_time(String br_time) {
		this.br_time = br_time;
	}

	public int getBr_duration() {
		return br_duration;
	}

	public void setBr_duration(int br_duration) {
		this.br_duration = br_duration;
	}

	public int getBr_mode() {
		return br_mode;
	}

	public void setBr_mode(int br_mode) {
		this.br_mode = br_mode;
	}

	public int getBr_repeat_num() {
		return br_repeat_num;
	}

	public void setBr_repeat_num(int br_repeat_num) {
		this.br_repeat_num = br_repeat_num;
	}

	@Override
	public String toString() {
		return "ReturnSetDataBroadcastOB [br_id=" + br_id + ", br_file_name="
				+ br_file_name + ", br_file=" + br_file + ", br_date="
				+ br_date + ", br_time=" + br_time + ", br_duration="
				+ br_duration + ", br_mode=" + br_mode + ", br_repeat_num="
				+ br_repeat_num + "]";
	}

}
