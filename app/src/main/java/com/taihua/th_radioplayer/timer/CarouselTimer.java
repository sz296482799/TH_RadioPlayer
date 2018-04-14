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
import com.taihua.th_radioplayer.domain.ReturnSetDataCarouselOB;
import com.taihua.th_radioplayer.domain.ReturnSetOB;

public class CarouselTimer {
	public static final int CAROUSEL_TIMER_MSG = 0x8001;
	private Handler mHandler = null;
	private Timer mTimer = null;
	private ArrayList<CarouselItem> mCarouseList = null;
	private boolean mIsRunning = false;
	
	public CarouselTimer(Handler handler) {
		// TODO Auto-generated constructor stub
		mTimer = new Timer();
		mCarouseList = new ArrayList<CarouselItem>();
		mHandler = handler;
	}
	
	public void init() {
		// TODO Auto-generated method stub
		ReturnSetOB returnSetOB = PlayerDB.getInstance().getServerData();
		if(returnSetOB != null) {
			setCarouselList(returnSetOB.getData().getCarousel());
		}
	}
	
	private ArrayList<Date> getCarouseDate(ReturnSetDataCarouselOB c, Date d) {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Date curDate = new Date();
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curDate);
		
		int curWeek = curCal.get(Calendar.DAY_OF_WEEK);
		int curHour = curCal.get(Calendar.HOUR_OF_DAY);
		int curMin = curCal.get(Calendar.MINUTE);
		int curSec = curCal.get(Calendar.SECOND);
		
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(d);
		int hour = startCal.get(Calendar.HOUR);
		int min = startCal.get(Calendar.MINUTE);
		int sec = startCal.get(Calendar.SECOND);
		
		if(curHour > hour || curMin > min || curSec > sec) {
			curCal.add(Calendar.DATE, 1);
			curWeek = curCal.get(Calendar.DAY_OF_WEEK);
		}
		
		curCal.set(Calendar.HOUR, hour);
		curCal.set(Calendar.MINUTE, min);
		curCal.set(Calendar.SECOND, sec);
		curDate = curCal.getTime();
		
		String[] weekList = c.getPlay_week().split(",");
		for(int i = 0; i < weekList.length; i++) {
			int w = Integer.parseInt(weekList[i]);
			curCal.setTime(curDate);
			curCal.add(Calendar.DATE, (w - curWeek + 7) % 7);
			dateList.add(curCal.getTime());
		}		
		return dateList;
	}
	
	private void setCarouse(ReturnSetDataCarouselOB c) {
		CarouselItem item = null;
		ArrayList<Date> dList = null;
		
		dList = getCarouseDate(c, new Date(c.getCa_start_time()));
		for(Date d : dList) {
			item = new CarouselItem(c);
			item.setDate(d, true);
			mCarouseList.add(item);
		}
		
		dList = getCarouseDate(c, new Date(c.getCa_end_time()));
		for(Date d : dList) {
			item = new CarouselItem(c);
			item.setDate(d, false);
			mCarouseList.add(item);
		}
	}
	
	private void resetTimer() {
		mTimer.cancel();
		mTimer.purge();
		mTimer = new Timer();
	}
	
	public void setCarouselList(List<ReturnSetDataCarouselOB> carouselList) {
		
		mCarouseList.clear();
		resetTimer();
		for(ReturnSetDataCarouselOB c : carouselList) {
			setCarouse(c);
		}
		
		if(mCarouseList.size() > 0) {
			Comparator<CarouselItem> com = new Comparator<CarouselItem>() {
	
				@Override
				public int compare(CarouselItem lhs, CarouselItem rhs) {
					// TODO Auto-generated method stub
					
					if(lhs.mDate.after(rhs.mDate))
						return 1;
					return -1;
				}
				
			};
			Collections.sort(mCarouseList, com);
			
			if(mIsRunning) {
				mTimer.schedule(new CarouseTask(mHandler, mTimer, mCarouseList), mCarouseList.get(0).mDate);
			}
		}
	}
	
	public void checkItem() {
		CarouselItem item = null;
		Date date = new Date();

		if(mCarouseList.size() == 0)
			return;

		do {
			item = mCarouseList.get(0);
			if(item != null && date.after(item.mDate)) {
				mCarouseList.remove(0);
			}
			else {
				break;
			}
		}while(true);
	}
	
	public void start() {
		
		if(!mIsRunning) {
			checkItem();
			if(mCarouseList.size() > 0)
				mTimer.schedule(new CarouseTask(mHandler, mTimer, mCarouseList), mCarouseList.get(0).mDate);
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
	
	public static class CarouselItem {
		private Date mDate = null;
		private boolean mIsStart = false;
		private int mCarouselID = -1;
		private int mPacketID = -1;
		private int mChannelID = -1;
		private int mPlayMode = -1;
		
		public CarouselItem(ReturnSetDataCarouselOB c) {
			// TODO Auto-generated constructor stub
			
			mCarouselID = c.getCa_id();
			mChannelID = c.getChannel_id();
			mPlayMode = c.getPlay_mode();
		}
		
		private void setDate(Date date, boolean isStart) {
			// TODO Auto-generated method stub
			mDate = date;
			mIsStart = isStart;
		}
		
		public Date getDate() {
			return mDate;
		}
		
		public boolean getIsStart() {
			return mIsStart;
		}
		
		public int getCarouselID() {
			return mCarouselID;
		}
		
		public int getPacketID() {
			return mPacketID;
		}
		
		public int getChannelID() {
			return mChannelID;
		}
		
		public int getPlayMode() {
			return mPlayMode;
		}
	}
	
	private class CarouseTask extends TimerTask {
		
		Handler mHandler = null;
		private Timer mTimer = null;
		private ArrayList<CarouselItem> mItemList = null;
		
		public CarouseTask(Handler handler, Timer timer, ArrayList<CarouselItem> itemList) {
			
			// TODO Auto-generated constructor stub
			mHandler = handler;
			mTimer = timer;
			mItemList = itemList;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mItemList.size() > 0) {
				CarouselItem item = mItemList.get(0);
				
				Message msg = new Message();
				msg.what = CAROUSEL_TIMER_MSG;
				msg.obj = item;
				mHandler.sendMessage(msg);
				
				mItemList.remove(0);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(item.mDate);
				cal.add(Calendar.DATE, 7);
				item.setDate(cal.getTime(), item.mIsStart);
				
				mItemList.add(item);
				
				if(mItemList.size() > 0) {
					CarouselItem next = mItemList.get(0);
					mTimer.schedule(new CarouseTask(mHandler, mTimer, mItemList), next.mDate);
				}
			}
		}
		
	}
}
