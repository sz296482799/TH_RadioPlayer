package com.taihua.th_radioplayer.domain;

import java.util.List;

public class ReturnActionOB {
	private int response_code;
	private List<ReturnActionDataOB> data;

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public List<ReturnActionDataOB> getData() {
		return data;
	}

	public void setData(List<ReturnActionDataOB> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnActionOB [response_code=" + response_code + ", data=" + data + "]";
	}

}
