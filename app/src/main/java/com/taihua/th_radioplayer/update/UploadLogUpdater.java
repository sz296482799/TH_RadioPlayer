package com.taihua.th_radioplayer.update;

import com.taihua.th_radioplayer.connection.ServerConnection;
import com.taihua.th_radioplayer.database.PlayerDB;
import com.taihua.th_radioplayer.domain.*;
import com.taihua.th_radioplayer.player.RadioChannel;
import com.taihua.th_radioplayer.utils.LogUtil;
import com.taihua.th_radioplayer.utils.MD5Util;

import java.util.*;

public class UploadLogUpdater {

	public static final String TAG = "UPLOAD_LOG_UPDATER";

    public static final int UPADTE_TYPE_START = 1;
    public static final int UPADTE_TYPE_FAIL = 2;
    public static final int UPADTE_TYPE_SUCC = 3;

    public static final int ACTION_TYPE_STARTPLAY = 1;
    public static final int ACTION_TYPE_STOPPLAY = 2;
    public static final int ACTION_TYPE_SWITCH = 3;
    public static final int ACTION_TYPE_VOLUP = 4;
    public static final int ACTION_TYPE_VOLDOWN= 5;
    public static final int ACTION_TYPE_BROADCAST = 6;
    public static final int ACTION_TYPE_CAROUSEL = 7;

	private static final int MIN_CYCLE_NUM = 1; //1 min

	private String mDeviceID = "UNKNOW";
	private Timer mTimer = null;
	private UpdateTask mTask = null;
	private int mCheckCycle = MIN_CYCLE_NUM;
	private boolean isRunning = false;
	private HashMap<RadioChannel, String> mUpdateMap;
    private RecordIB mRecordIB;
    private boolean isUploadAction = false;
    private boolean isUploadUpdate = false;

	public UploadLogUpdater(String deviceID) {
		// TODO Auto-generated constructor stub
		mDeviceID = deviceID;
        mUpdateMap = new HashMap<>();
        mRecordIB = new RecordIB();
	}

    public void init() {
        // TODO Auto-generated method stub
        BaseDataIB baseDataIB = PlayerDB.getInstance().getBaseData();
        if(baseDataIB != null) {
            mCheckCycle = baseDataIB.getData().getBase().getBox_log_cycle();
        }
    }

	private void start(int delay) {
		if(isRunning) return;
		if(mTimer == null) {
			mTimer = new Timer();
		}
		if(mTask == null) {
			mTask = new UpdateTask();
		}
		mTimer.schedule(mTask, delay, mCheckCycle * 60000);
		isRunning = true;
	}

	public void reset(int delay) {
		stop();
		start(delay);
	}

	public void setCycle(int cycle) {
		if(cycle <= MIN_CYCLE_NUM)
			cycle = MIN_CYCLE_NUM;

		if(mCheckCycle == cycle)
			return;

		mCheckCycle = cycle;
		reset(mCheckCycle * 60000);
		LogUtil.d(TAG, "setCycle:" + mCheckCycle);
	}

	public void start() {
		start(mCheckCycle * 60000);
	}

	public void stop() {
		if(!isRunning) return;
		if(mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
		if(mTask != null) {
			mTask.cancel();
		}
		mTimer = null;
		mTask = null;
		isRunning = false;
	}

    public void setIsUploadAction(boolean uploadAction) {
        isUploadAction = uploadAction;
    }

    public boolean getIsUploadAction() {
	    return isUploadAction;
    }

    public void setIsUploadUpdate(boolean uploadUpdate) {
        isUploadUpdate = uploadUpdate;
    }

    public boolean getIsUploadUpdate() {
        return isUploadUpdate;
    }

    public void addAction(int channelID, int actionID, int actionType) {
	    if(!isUploadUpdate)
	        return;

        long logTime = new Date().getTime();

        RecordActionIB action = new RecordActionIB();

        action.setHardware_num(mDeviceID);
        action.setAction_type(actionType);
        action.setAction_id(actionID);
        action.setSource_id(channelID);
        action.setLog_add_time(logTime);

        mRecordIB.addAction(action);
    }

    public void addUpdate(RadioChannel channel, int updateType) {
	    if(!isUploadUpdate || channel == null)
	        return;

        long logTime = new Date().getTime();

        String updateID = null;

        if(UPADTE_TYPE_START == updateType) {
            updateID = MD5Util.MD5("" + logTime);
            mUpdateMap.put(channel, updateID);
        }
        else {
            updateID = mUpdateMap.get(channel);
        }

        RecordUpdateActionIB action = new RecordUpdateActionIB();

        action.setAction_id(updateType);
        action.setLog_add_time(logTime);
        action.setHardware_num(mDeviceID);
        action.setUpdate_id(updateID);

        mRecordIB.addUpdateAction(action);

        if(UPADTE_TYPE_START != updateType) {
            RecordUpdateLogIB log = new RecordUpdateLogIB();

            log.setHardware_num(mDeviceID);
            log.setUpdate_id(updateID);
            log.setChannel_id(channel.getChannelID());
            log.setReal_num(channel.getDownloadSuccNum());
            log.setTotal_num(channel.size());
            log.setLog_add_time(logTime);

            mRecordIB.addUpdateLog(log);
            mUpdateMap.remove(channel);
        }
    }

	private class UpdateTask extends TimerTask {

		@Override
		public void run() {
            RecordIB recordIB = new RecordIB();
            if (isUploadAction) {
                recordIB.setAction(mRecordIB.getAction());
            }

            if (isUploadUpdate) {
                recordIB.setUpdate(mRecordIB.getUpdate());
            }

            if(recordIB.getAction().size() > 0
                    || recordIB.getUpdate().getLog().size() > 0
                    || recordIB.getUpdate().getAction().size() > 0) {
                ReturnOB returnOB = ServerConnection.getInstance().setBoxLog(mDeviceID, recordIB);
                if (returnOB == null) {
                    return;
                }

                if (isUploadAction) {
                    mRecordIB.clearActions();
                }

                if (isUploadUpdate) {
                    mRecordIB.clearUpdate();
                }
            }
		}
	}
}
