package com.taihua.th_radioplayer.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.taihua.th_radioplayer.database.PlayerDB;
import com.taihua.th_radioplayer.domain.ReturnSetDataBroadcastOB;
import com.taihua.th_radioplayer.domain.ReturnSetOB;

public class BroadcastTimer {
	public static final int BROADCAST_TIMER_MSG = 0x8002;
	private Handler mHandler = null;
	private Timer mTimer = null;
	private ArrayList<BroadcastItem> mBroadcastList = null;
	private boolean mIsRunning = false;
	
	public BroadcastTimer(Handler handler) {
		// TODO Auto-generated constructor stub
		mTimer = new Timer();
		mBroadcastList = new ArrayList<BroadcastItem>();
		mHandler = handler;
	}
	
	public void init() {
		// TODO Auto-generated method stub
		ReturnSetOB returnSetOB = PlayerDB.getInstance().getServerData();
		if(returnSetOB != null) {
			setBroadcastList(returnSetOB.getData().getBroadcast());
		}
	}
	
	private ArrayList<Date> getBroadcastDate(ReturnSetDataBroadcastOB b, Date d) {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Date curDate = new Date();
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curDate);
		
		int curWeek = curCal.get(Calendar.DAY_OF_WEEK);
		int curHour = curCal.get(Calendar.HOUR_OF_DAY);
		int curMin = curCal.get(Calendar.MINUTE);
		int curSec = curCal.get(Calendar.SECOND);
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.setTime(d);
		int hour = dateCal.get(Calendar.HOUR);
		int min = dateCal.get(Calendar.MINUTE);
		int sec = dateCal.get(Calendar.SECOND);
		
		if(curHour > hour || curMin > min || curSec > sec) {
			curCal.add(Calendar.DATE, 1);
			curWeek = curCal.get(Calendar.DAY_OF_WEEK);
		}
		
		curCal.set(Calendar.HOUR, hour);
		curCal.set(Calendar.MINUTE, min);
		curCal.set(Calendar.SECOND, sec);
		curDate = curCal.getTime();
		
		String[] weekList = b.getBr_date().split(",");
		for(int i = 0; i < weekList.length; i++) {
			int w = Integer.parseInt(weekList[i]);
			curCal.setTime(curDate);
			curCal.add(Calendar.DATE, (w - curWeek + 7) % 7);
			dateList.add(curCal.getTime());
		}		
		return dateList;
	}
	
	private void setBroadcast(ReturnSetDataBroadcastOB b) {
		BroadcastItem item = null;
		ArrayList<Date> dList = null;

		String[] times = b.getBr_date().split(",");
		for (int i = 0; i < times.length; i++) {
            dList = getBroadcastDate(b, new Date(Integer.parseInt(times[i], 10)));
            for(Date d : dList) {
                item = new BroadcastItem(b);
                item.setDate(d);
                mBroadcastList.add(item);
            }
        }
	}
	
	private void resetTimer() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
		mTimer = new Timer();
	}
	
	public void setBroadcastList(List<ReturnSetDataBroadcastOB> broadcastList) {
		
		mBroadcastList.clear();
		resetTimer();
		for(ReturnSetDataBroadcastOB b : broadcastList) {
			setBroadcast(b);
		}
		
		if(mBroadcastList.size() > 0) {
			Comparator<BroadcastItem> com = new Comparator<BroadcastItem>() {
	
				@Override
				public int compare(BroadcastItem lhs, BroadcastItem rhs) {
					// TODO Auto-generated method stub
					
					if(lhs.mDate.after(rhs.mDate))
						return 1;
					return -1;
				}
				
			};
			Collections.sort(mBroadcastList, com);
			
			if(mIsRunning) {
				mTimer.schedule(new BroadcastTask(mHandler, mTimer, mBroadcastList), mBroadcastList.get(0).mDate);
			}
		}
	}
	
	public void checkItem() {
		BroadcastItem item = null;
		Date date = new Date();

		if(mBroadcastList.size() == 0)
		    return;

		do {
			item = mBroadcastList.get(0);
			if(item != null && date.after(item.mDate)) {
				mBroadcastList.remove(0);
			}
			else {
				break;
			}
		}while(true);
	}
	
	public void start() {
		
		if(!mIsRunning) {
			checkItem();
			if(mBroadcastList.size() > 0)
				mTimer.schedule(new BroadcastTask(mHandler, mTimer, mBroadcastList), mBroadcastList.get(0).mDate);
			mIsRunning = true;
		}
	}
	
	public void stop() {
		resetTimer();
		mIsRunning = false;
	}
	
	public boolean isStart() {
		return mIsRunning;
	}
	
	public static class BroadcastItem {
		private Date mDate = null;
		private int mBroadcastID = -1;
		private int mPlayMode = -1;
		public int mRepeatNum = 0;
		private String mFileName = null;
		private String mFileUrl = null;
		
		public BroadcastItem(ReturnSetDataBroadcastOB b) {
			// TODO Auto-generated constructor stub
			
			mBroadcastID = b.getBr_id();
			mPlayMode = b.getBr_mode();
			mFileName = b.getBr_file_name();
			mFileUrl = b.getBr_file();
			mRepeatNum = b.getBr_repeat_num();
		}
		
		private void setDate(Date date) {
			// TODO Auto-generated method stub
			mDate = date;
		}
		
		public Date getDate() {
			return mDate;
		}
		
		public int getBroadcastID() {
			return mBroadcastID;
		}
		
		public int getPlayMode() {
			return mPlayMode;
		}
		
		public String getFileName() {
			return mFileName;
		}
		
		public String getFileUrl() {
			return mFileUrl;
		}
	}
	
	private class BroadcastTask extends TimerTask {
		
		Handler mHandler = null;
		private Timer mTimer = null;
		private ArrayList<BroadcastItem> mItemList = null;
		
		public BroadcastTask(Handler handler, Timer timer, ArrayList<BroadcastItem> itemList) {
			
			// TODO Auto-generated constructor stub
			mHandler = handler;
			mTimer = timer;
			mItemList = itemList;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mItemList.size() > 0) {
				BroadcastItem item = mItemList.get(0);
				
				Message msg = new Message();
				msg.what = BROADCAST_TIMER_MSG;
				msg.obj = item;
				mHandler.sendMessage(msg);
				
				mItemList.remove(0);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(item.mDate);
				cal.add(Calendar.DATE, 7);
				item.setDate(cal.getTime());
				
				mItemList.add(item);
				
				if(mItemList.size() > 0) {
					BroadcastItem next = mItemList.get(0);
					mTimer.schedule(new BroadcastTask(mHandler, mTimer, mItemList), next.mDate);
				}
			}
		}
		
	}
}
