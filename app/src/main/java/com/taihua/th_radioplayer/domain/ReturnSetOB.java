package com.taihua.th_radioplayer.domain;

public class ReturnSetOB {
	// 1 sucess 0 error
	private int response_code;
	private ReturnSetDataOB data;

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public ReturnSetDataOB getData() {
		return data;
	}

	public void setData(ReturnSetDataOB data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ReturnSetOB [response_code=" + response_code + ", data=" + data
				+ "]";
	}

}
