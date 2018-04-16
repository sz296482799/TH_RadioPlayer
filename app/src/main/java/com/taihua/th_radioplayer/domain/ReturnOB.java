package com.taihua.th_radioplayer.domain;

public class ReturnOB {
	//1 sucess  0 error
	private int response_code;
	private String client_secret;
	private String msg;
	
	public int getResponse_code() {
		return response_code;
	}
	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "InitclientOutputBean [response_code=" + response_code
				+ ", client_secret=" + client_secret + ", msg=" + msg + "]";
	}
	

}
