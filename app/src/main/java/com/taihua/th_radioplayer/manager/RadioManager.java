package com.taihua.th_radioplayer.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import com.taihua.th_radioplayer.database.PlayerDB;
import com.taihua.th_radioplayer.domain.BaseDataDataIteamIB;
import com.taihua.th_radioplayer.domain.ReturnMusicChannelOB;
import com.taihua.th_radioplayer.download.DownloadInfo;
import com.taihua.th_radioplayer.download.DownloadManager;
import com.taihua.th_radioplayer.download.DownloadState;
import com.taihua.th_radioplayer.player.RadioChannel;
import com.taihua.th_radioplayer.player.RadioDownloadHolder;
import com.taihua.th_radioplayer.player.RadioItem;
import com.taihua.th_radioplayer.player.RadioPacket;
import com.taihua.th_radioplayer.utils.LogUtil;
import org.xutils.ex.DbException;

@SuppressLint("UseSparseArrays")
public class RadioManager {

    public static final String TAG = "RADIO_MANAGER";

    public static final int RADIO_DOWNLOAD_START = 0x1001;
    public static final int RADIO_DOWNLOAD_END = 0x1002;
	
	private static RadioManager mInstance;
	private HashMap<Integer, RadioPacket> mPacketMap;
	private HashMap<Integer, RadioChannel> mChannelMap;
	private HashMap<RadioItem, RadioDownloadHolder> mDownloadMap;
	private boolean mIsChange = false;
	private Handler mHandler;
	
	private RadioManager() {
		mChannelMap = new HashMap<>();
		mPacketMap = new HashMap<>();
        mDownloadMap = new HashMap<>();
	}

	public static RadioManager getInstance() {

		if (mInstance == null) {
			synchronized (RadioManager.class) {
				if (mInstance == null) {
					mInstance = new RadioManager();
				}
			}
		}
		return mInstance;
	}
	
	public void init(Handler handler) {
		// TODO Auto-generated method stub

        mHandler = handler;

		mChannelMap = PlayerDB.getInstance().getChannelData();
        LogUtil.d(TAG, "Channel Map:" + mChannelMap);
		if(mChannelMap == null) {
            mChannelMap = new HashMap<Integer, RadioChannel>();
        }
		else {
			Iterator<Entry<Integer, RadioChannel>> citer = mChannelMap.entrySet().iterator();
			while (citer.hasNext()) {
				downloadAllRadio(citer.next().getValue());
			}
		}
	}

	private void sendMessage(int what, Object obj) {
	    if(mHandler != null) {
            Message msg = new Message();
            msg.what = what;
            msg.obj = obj;
	        mHandler.sendMessage(msg);
        }
    }
	
	private boolean addPacket(RadioPacket packet) {
		if(packet == null)
			return false;
		
		if((getPacketByID(packet.getPacketID())) != null) {
			return false;
		}
		return mPacketMap.put(packet.getPacketID(), packet) != null;
	}
	
	private boolean addChannel(RadioChannel channel) {
		// TODO Auto-generated method stub
		if(channel == null)
			return false;
		if(getChannelByID(channel.getChannelID()) != null)
			return false;
		return mChannelMap.put(channel.getChannelID(), channel) != null;
	}
	
	private String getSavePath(RadioItem item) {
		String path = null;
		if(item != null) {
			RadioChannel c = getChannelByID(item.getChannelID());
			if(c != null) {
				path = RadioStorageManager.getInstance().getSavePath();
				path += "/" + c.getChannelID() + "-" + c.getChannelName();
				path += "/" + item.getRadioID() + "-" + item.getRadioName();
			}
		}
		return path;
	}
	
	private void delOneRadio(RadioItem item) {
    	if(item != null) {
            try {
                RadioDownloadHolder holder = mDownloadMap.get(item);
                if(holder != null) {
                    DownloadInfo info = holder.getDownloadInfo();
                    if(info != null) {
                        DownloadManager.getInstance().removeDownload(info);
                        mDownloadMap.remove(item);
                        return;
                    }
                }
                RadioStorageManager.getInstance().delFile(getSavePath(item));
            } catch (DbException e) {
                e.printStackTrace();
            }
    	}
	}
	
	private void delAllRadio(RadioChannel channel) {
		ArrayList<RadioItem> radiosList = channel.getRadios();

		for(int i = 0; i < radiosList.size(); i++) {
			delOneRadio(radiosList.get(i));
		}
		radiosList.clear();
	}

	private void downloadOneRadio(RadioItem item) {
		if(item == null)
			return;

        try {
            if(item.getDowanloadStatus().value() > DownloadState.STARTED.value()) {
                return;
            }
            String savePath = getSavePath(item);

            item.setSavePath(savePath);

            RadioDownloadHolder holder = new RadioDownloadHolder(this, item, new DownloadInfo());
            mDownloadMap.put(item, holder);
            DownloadManager.getInstance().startDownload(
                    item.getDowanloadUrl(),
                    item.getRadioName(),
                    savePath,
                    false,
                    false,
                    holder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
	
	private boolean downloadAllRadio(RadioChannel channel) {
		if(channel == null)
			return false;

		if(channel.getRadios() == null || channel.getRadios().size() == 0)
		    return false;

		ArrayList<RadioItem> radiosList = channel.getRadios();
		for(int i = 0; i < radiosList.size(); i++) {
			downloadOneRadio(radiosList.get(i));
		}
		return true;
	}

	public void touchDownloadFinish(RadioItem item, DownloadInfo info) {
        if(item != null) {

            mIsChange = true;

            item.setDowanloadStatus(info.getState());
            RadioChannel channel = getChannelByID(item.getChannelID());
            if(channel != null) {
                channel.downloadOne(item);
                if(channel.isFinish()) {
                    sendMessage(RADIO_DOWNLOAD_END, channel);
                    updateDB();
                }
            }
            mDownloadMap.remove(item);
        }
    }
	
	public synchronized RadioPacket getPacketByID(int packetID) {
		// TODO Auto-generated method stub
		return mPacketMap.get(packetID);
	}
	
	public synchronized RadioChannel getChannelByID(int channelID) {
		return mChannelMap.get(channelID);
	}

	public synchronized List<RadioChannel> getChannelList() {
	    ArrayList<RadioChannel> list = new ArrayList<RadioChannel>();

        Iterator<Entry<Integer, RadioChannel>> iter = mChannelMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, RadioChannel> entry = iter.next();
            RadioChannel c = entry.getValue();
            if(c != null) {
                RadioChannel channel = new RadioChannel();

                channel.setChannelID(c.getChannelID());
                channel.setChannelPic(c.getChannelPic());
                channel.setChannelName(c.getChannelName());
                channel.setChannelVer(c.getChannelVer());
                channel.setRadioNum(c.getRadioNum());

                list.add(channel);
            }
        }
		return list;
	}
	
	public void clearPacket() {
		// TODO Auto-generated method stub
		mPacketMap.clear();
	}
	
	public synchronized void updatePacket(BaseDataDataIteamIB p) {
		// TODO Auto-generated method stub
		RadioPacket packet = getPacketByID(p.getPks_id());
		if(packet == null || packet.getPacketVer() != p.getPks_ver()) {
			if(packet == null) {
				packet = new RadioPacket(p.getPks_id(), p.getPks_ver());
				addPacket(packet);
			}
			packet.update(p);
		}
	}

	public synchronized void updateChannel(ReturnMusicChannelOB c) {
		// TODO Auto-generated method stub
		RadioChannel channel = getChannelByID(c.getChannel_id());
		if(channel == null) {
			channel = new RadioChannel(c.getChannel_id(), c.getChannel_ver());
			channel.setChannelName(c.getChannel_name());
			channel.setChannelPic(c.getChannel_pic());
			addChannel(channel);
		}
		delAllRadio(channel);
		channel.update(c);
		if(downloadAllRadio(channel)) {
		    sendMessage(RADIO_DOWNLOAD_START, channel);
        }
		mIsChange = true;
	}
	
	public void bindChannel() {
		// TODO Auto-generated method stub
		if(mChannelMap.isEmpty() || mPacketMap.isEmpty()) {
			return;
		}
		
		Iterator<Entry<Integer, RadioChannel>> citer = mChannelMap.entrySet().iterator();
		while (citer.hasNext()) {
			Entry<Integer, RadioChannel> entry = citer.next();
			RadioChannel c = entry.getValue();
        	if(c != null) {
        		c.getHasPacketList().clear();
        	}
		}
		
		Iterator<Entry<Integer, RadioPacket>> piter = mPacketMap.entrySet().iterator();
		while (piter.hasNext()) {
			Entry<Integer, RadioPacket> pentry = piter.next();
			RadioPacket p = pentry.getValue();
        	if(p != null) {
                ArrayList<Integer> hasChannelList = p.getHasChannelList();
                for(Integer i : hasChannelList) {
                    RadioChannel c = getChannelByID(i);
                    if(c != null) {
                        c.getHasPacketList().add(p.getPacketID());
                    }
                }
        	}
		}
	}

	public void updateDB() {
		// TODO Auto-generated method stub
		if(mIsChange) {
            LogUtil.d(TAG, "UpdateDB Channel Map:" + mChannelMap);
			PlayerDB.getInstance().writeChannelData(mChannelMap);
		}
	}

	public void clearUnusedChannel() {
		// TODO Auto-generated method stub
		Iterator<Entry<Integer, RadioChannel>> citer = mChannelMap.entrySet().iterator();
		while (citer.hasNext()) {
			Entry<Integer, RadioChannel> entry = citer.next();
			RadioChannel c = getChannelByID(entry.getKey());
			if(isUnused(c)) {
				citer.remove();
				mIsChange = true;
			}
		}
	}

	private boolean isUnused(RadioChannel c) {
		// TODO Auto-generated method stub
		return c != null && c.getHasPacketList().isEmpty();
	}
}
