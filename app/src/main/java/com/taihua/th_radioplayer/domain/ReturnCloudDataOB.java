package com.taihua.th_radioplayer.domain;

public class ReturnCloudDataOB {
	private int cloud_id;
	private String cloud_code;
	private String cloud_title;
	private String download_url;

	public int getCloud_id() {
		return cloud_id;
	}

	public void setCloud_id(int cloud_id) {
		this.cloud_id = cloud_id;
	}

	public String getCloud_code() {
		return cloud_code;
	}

	public void setCloud_code(String cloud_code) {
		this.cloud_code = cloud_code;
	}

	public String getCloud_title() {
		return cloud_title;
	}

	public void setCloud_title(String cloud_title) {
		this.cloud_title = cloud_title;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	@Override
	public String toString() {
		return "ReturnCloudDataOB [cloud_id=" + cloud_id + ", cloud_code="
				+ cloud_code + ", cloud_title=" + cloud_title
				+ ", download_url=" + download_url + "]";
	}

}
