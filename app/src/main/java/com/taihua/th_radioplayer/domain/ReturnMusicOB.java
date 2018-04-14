package com.taihua.th_radioplayer.domain;

import java.util.List;

public class ReturnMusicOB {
	// 1 sucess 0 error
	private int response_code;
	private List<ReturnMusicChannelOB> data;

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public List<ReturnMusicChannelOB> getData() {
		return data;
	}

	public void setData(List<ReturnMusicChannelOB> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnMusicOB [response_code=" + response_code + ", data="
				+ data + "]";
	}

	
	
}
