package com.taihua.th_radioplayer.domain;

import java.util.List;

public class ReturnMusicChannelOB {
	private int channel_id;
	private int channel_ver;
	private String channel_name;
	private String channel_pic;
	private int music_count;
	private List<ReturnMusicChannelMusicOB> musics;

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}
	
	public int getChannel_ver() {
		return channel_ver;
	}

	public void setChannel_ver(int channel_ver) {
		this.channel_ver = channel_ver;
	}

	public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	public String getChannel_pic() {
		return channel_pic;
	}

	public void setChannel_pic(String channel_pic) {
		this.channel_pic = channel_pic;
	}

	public int getMusic_count() {
		return music_count;
	}

	public void setMusic_count(int music_count) {
		this.music_count = music_count;
	}

	public List<ReturnMusicChannelMusicOB> getMusics() {
		return musics;
	}

	public void setMusics(List<ReturnMusicChannelMusicOB> musics) {
		this.musics = musics;
	}

	@Override
	public String toString() {
		return "ReturnMusicChannelOB [channel_id=" + channel_id
				+ ", channel_name=" + channel_name 
				+ ", channel_ver=" + channel_ver
				+ ", channel_pic=" + channel_pic
				+ ", music_count=" + music_count
				+ ", musics=" + musics
				+ "]";
	}

	

}
