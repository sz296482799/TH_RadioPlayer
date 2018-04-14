package com.taihua.th_radioplayer.domain;

public class InitclientIB {
	
	private String hardware_num;
	private String client_type;
	
	public InitclientIB(String hardware_num,String client_type){
		this .hardware_num=hardware_num;
		this.client_type =client_type;
	}
	
	
	public String getHardware_num() {
		return hardware_num;
	}
	public void setHardware_num(String hardware_num) {
		this.hardware_num = hardware_num;
	}
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
