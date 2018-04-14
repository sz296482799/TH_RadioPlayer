package com.taihua.th_radioplayer.domain;

import java.util.List;

public class ReturnCloudOB {
	// 1 sucess 0 error
	private int response_code;
	private List<ReturnCloudDataOB> data;

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public List<ReturnCloudDataOB> getData() {
		return data;
	}

	public void setData(List<ReturnCloudDataOB> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnCloudOB [response_code=" + response_code + ", data="
				+ data + "]";
	}

}
