package com.taihua.th_radioplayer.domain;

public class BaseDataDataBaseIB {

	private int client_id;
	private String client_code;
	private String hardware_num;
	private int is_carousel;
	private int is_edit_carouse;
	private int is_broadcast;
	private int is_edit_broadcast;
	private int is_action_log_upload;
	private int is_update_log_upload;
	private int box_log_cycle;
	private int box_check_cycle;

	public int getClient_id() {
		return client_id;
	}

	public void setClient_id(int client_id) {
		this.client_id = client_id;
	}

	public String getClient_code() {
		return client_code;
	}

	public void setClient_code(String client_code) {
		this.client_code = client_code;
	}

	public String getHardware_num() {
		return hardware_num;
	}

	public void setHardware_num(String hardware_num) {
		this.hardware_num = hardware_num;
	}

	public int getIs_carousel() {
		return is_carousel;
	}

	public void setIs_carousel(int is_carousel) {
		this.is_carousel = is_carousel;
	}

	public int getIs_edit_carouse() {
		return is_edit_carouse;
	}

	public void setIs_edit_carouse(int is_edit_carouse) {
		this.is_edit_carouse = is_edit_carouse;
	}

	public int getIs_broadcast() {
		return is_broadcast;
	}

	public void setIs_broadcast(int is_broadcast) {
		this.is_broadcast = is_broadcast;
	}

	public int getIs_edit_broadcast() {
		return is_edit_broadcast;
	}

	public void setIs_edit_broadcast(int is_edit_broadcast) {
		this.is_edit_broadcast = is_edit_broadcast;
	}

	public int getIs_action_log_upload() {
		return is_action_log_upload;
	}

	public void setIs_action_log_upload(int is_action_log_upload) {
		this.is_action_log_upload = is_action_log_upload;
	}

	public int getIs_update_log_upload() {
		return is_update_log_upload;
	}

	public void setIs_update_log_upload(int is_update_log_upload) {
		this.is_update_log_upload = is_update_log_upload;
	}

	public int getBox_log_cycle() {
		return box_log_cycle;
	}

	public void setBox_log_cycle(int box_log_cycle) {
		this.box_log_cycle = box_log_cycle;
	}

	public int getBox_check_cycle() {
		return box_check_cycle;
	}

	public void setBox_check_cycle(int box_check_cycle) {
		this.box_check_cycle = box_check_cycle;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		BaseDataDataBaseIB base = (BaseDataDataBaseIB)o;
		if(base.client_id != client_id)
			return false;
		else if(base.client_code != client_code) {
			return false;
		}
		else if(base.hardware_num != hardware_num) {
			return false;
		}
		else if(base.is_carousel != is_carousel) {
			return false;
		}
		else if(base.is_edit_carouse != is_edit_carouse) {
			return false;
		}
		else if(base.is_broadcast != is_broadcast) {
			return false;
		}
		else if(base.is_edit_broadcast != is_edit_broadcast) {
			return false;
		}
		else if(base.is_action_log_upload != is_action_log_upload) {
			return false;
		}
		else if(base.is_update_log_upload != is_update_log_upload) {
			return false;
		}
		else if(base.box_log_cycle != box_log_cycle) {
			return false;
		}
		else if(base.box_check_cycle != box_check_cycle) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BaseDataBaseInputBean [client_id=" + client_id
				+ ", client_code=" + client_code + ", hardware_num="
				+ hardware_num + ", is_carousel=" + is_carousel
				+ ", is_edit_carouse=" + is_edit_carouse + ", is_broadcast="
				+ is_broadcast + ", is_edit_broadcast=" + is_edit_broadcast
				+ ", is_action_log_upload=" + is_action_log_upload
				+ ", is_update_log_upload=" + is_update_log_upload
				+ ", box_log_cycle=" + box_log_cycle + ", box_check_cycle="
				+ box_check_cycle + "]";
	}
	

}
