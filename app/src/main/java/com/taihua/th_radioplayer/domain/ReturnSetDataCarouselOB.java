package com.taihua.th_radioplayer.domain;

public class ReturnSetDataCarouselOB {
	private int ca_id;
	private int channel_id;
	private String play_week;
	private int play_mode;
	private int is_refuse;
	private int ca_start_time;
	private int ca_end_time;

	public int getCa_id() {
		return ca_id;
	}

	public void setCa_id(int ca_id) {
		this.ca_id = ca_id;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getPlay_week() {
		return play_week;
	}

	public void setPlay_week(String play_week) {
		this.play_week = play_week;
	}

	public int getPlay_mode() {
		return play_mode;
	}

	public void setPlay_mode(int play_mode) {
		this.play_mode = play_mode;
	}

	public int getIs_refuse() {
		return is_refuse;
	}

	public void setIs_refuse(int is_refuse) {
		this.is_refuse = is_refuse;
	}

	public int getCa_start_time() {
		return ca_start_time;
	}

	public void setCa_start_time(int ca_start_time) {
		this.ca_start_time = ca_start_time;
	}

	public int getCa_end_time() {
		return ca_end_time;
	}

	public void setCa_end_time(int ca_end_time) {
		this.ca_end_time = ca_end_time;
	}

	@Override
	public String toString() {
		return "ReturnSetDataCarouselOB [ca_id=" + ca_id + ", channel_id="
				+ channel_id + ", play_week=" + play_week + ", play_mode="
				+ play_mode + ", is_refuse=" + is_refuse + ", ca_start_time="
				+ ca_start_time + ", ca_end_time=" + ca_end_time + "]";
	}

	
	
	
}
