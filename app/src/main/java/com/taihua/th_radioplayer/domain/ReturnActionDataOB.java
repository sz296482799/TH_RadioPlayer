package com.taihua.th_radioplayer.domain;

public class ReturnActionDataOB {
	private int action_id;
	private String action_name;

	public int getAction_id() {
		return action_id;
	}

	public void setAction_id(int action_id) {
		this.action_id = action_id;
	}

	public String getAction_name() {
		return action_name;
	}

	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}

	@Override
	public String toString() {
		return "ReturnActionOB [action_id=" + action_id + ", action_name="
				+ action_name + "]";
	}

}
