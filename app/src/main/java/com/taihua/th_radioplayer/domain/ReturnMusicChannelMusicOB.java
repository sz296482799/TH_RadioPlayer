package com.taihua.th_radioplayer.domain;

public class ReturnMusicChannelMusicOB {
	private int music_id;
	private String name;
	private String album;
	private String singer;
	private String download_url;

	public int getMusic_id() {
		return music_id;
	}

	public void setMusic_id(int music_id) {
		this.music_id = music_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	@Override
	public String toString() {
		return "ReturnMusicChannelMusicOB [music_id=" + music_id + ", name="
				+ name + ", album=" + album + ", singer=" + singer
				+ ", download_url=" + download_url + "]";
	}

}
