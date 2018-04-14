package com.taihua.th_radioplayer.domain;

import java.util.ArrayList;
import java.util.List;

public class RecordIB {

    private static final int MAX_ACTION_NUM = 30;
    private static final int MAX_UPDATE_NUM = 15; //double num

	private List<RecordActionIB> actions;
	private RecordUpdateIB update;

	public RecordIB() {
        this.actions = new ArrayList<>();
        this.update = new RecordUpdateIB();
    }

	public List<RecordActionIB> getAction() {
		return actions;
	}

	public void setAction(List<RecordActionIB> actions) {
		this.actions = actions;
	}

	public RecordUpdateIB getUpdate() {
		return update;
	}

	public void setUpdate(RecordUpdateIB update) {
		this.update = update;
	}

    public boolean addAction(RecordActionIB action) {
        if(action != null) {
            if (actions.size() > MAX_ACTION_NUM)
                actions.remove(0);
            return actions.add(action);
        }
        return false;
    }

    public boolean addUpdateLog(RecordUpdateLogIB log) {

        if(log != null) {
            if (update.getLog().size() > MAX_UPDATE_NUM) {
                update.getLog().remove(0);
            }
            return update.getLog().add(log);
        }
        return false;
    }

    public boolean addUpdateAction(RecordUpdateActionIB action) {

        if(action != null) {
            if (update.getAction().size() > MAX_UPDATE_NUM) {
                update.getAction().remove(0);
            }
            return update.getAction().add(action);
        }
        return false;
    }

    public void clearActions() {
        actions.clear();
    }

    public void clearUpdate() {
        update.getAction().clear();
        update.getLog().clear();
    }

	@Override
	public String toString() {
		return "RecordInputBean [actions=" + actions + ", update=" + update + "]";
	}

}
