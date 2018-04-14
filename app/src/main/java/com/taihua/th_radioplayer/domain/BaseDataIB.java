package com.taihua.th_radioplayer.domain;


public class BaseDataIB {

	private int response_code;
	private BaseDataDataIB data;

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public BaseDataDataIB getData() {
		return data;
	}

	public void setData(BaseDataDataIB data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "BaseDataInputBean [response_code=" + response_code + ", data="
				+ data + "]";
	}

}
