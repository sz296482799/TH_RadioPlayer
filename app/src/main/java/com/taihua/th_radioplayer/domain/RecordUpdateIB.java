package com.taihua.th_radioplayer.domain;

import java.util.ArrayList;
import java.util.List;

public class RecordUpdateIB {

	private List<RecordUpdateLogIB> log;
	private List<RecordUpdateActionIB> action;

	public RecordUpdateIB() {
        this.log = new ArrayList<>();
        this.action = new ArrayList<>();
    }

	public List<RecordUpdateLogIB> getLog() {
		return log;
	}

	public void setLog(List<RecordUpdateLogIB> log) {
		this.log = log;
	}

	public List<RecordUpdateActionIB> getAction() {
		return action;
	}

	public void setAction(List<RecordUpdateActionIB> action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "RecordUpdateInputBean [log=" + log + ", action=" + action + "]";
	}

}
