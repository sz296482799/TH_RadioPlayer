package com.taihua.th_radioplayer.manager;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.storage.StorageManager;
//import android.os.storage.StorageVolume;

public class RadioStorageManager {

	public static final String RAIDOS_DIR_NAME = "radios";
	private static RadioStorageManager mInstance;
	private Context mContext = null;
	private StorageManager mStorageManager = null;
	
	private RadioStorageManager() {

	}

	public static RadioStorageManager getInstance() {
		if (mInstance == null) {
			synchronized (RadioStorageManager.class) {
				if (mInstance == null) {
					mInstance = new RadioStorageManager();
				}
			}
		}
		return mInstance;
	}
	
	public RadioStorageManager init(Context context) {
		// TODO Auto-generated constructor stub
		
		mContext = context;
		mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		return mInstance;
	}
	
	public String getSavePath() {
		if(mContext == null)
			return "";
		return mContext.getFilesDir().getPath() + "/" + RAIDOS_DIR_NAME;
	}
	
	public boolean delFile(File file) {
		if(file.exists() && file.isFile()) {
			return file.delete(); 
		}
		return false;
	}
	
	public boolean delFile(String fileName) {
		return delFile(new File(fileName));
	}
	
	public USBNode getUSBNode(USBNode node) throws NoSuchMethodException {
		// TODO Auto-generated constructor stub
		/*StorageVolume[] storageVolumes;
		if(node == null) {
			
			Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
			storageVolumes = (StorageVolume[]) getVolumeList.invoke(storageManager);
			return null;
		}*/
		return null;
	}
	
	static public class USBNode {
		
		public static final int NODE_TYPE_ROOT = 0;
		public static final int NODE_TYPE_DEVICE = 1;
		public static final int NODE_TYPE_DER = 2;
		public static final int NODE_TYPE_FILE = 3;
		
		private String mName = null;
		private int mType = -1;
		private Object mPrivateData = null;
		private ArrayList<USBNode> mList = null;
		
		public USBNode() {
			// TODO Auto-generated constructor stub
			
			mName = "UNKNOW";
			mList = new ArrayList<USBNode>();
		}
		
		public void setName(String name) {
			// TODO Auto-generated method stub
			mName = name;
		}
		
		public String getName() {
			// TODO Auto-generated method stub
			return mName;
		}
		
		public void setType(int type) {
			// TODO Auto-generated method stub
			mType = type;
		}
		
		public int getType() {
			// TODO Auto-generated method stub
			return mType;
		}
		
		public void setData(Object data) {
			// TODO Auto-generated method stub
			mPrivateData = data;
		}
		
		public Object getData() {
			// TODO Auto-generated method stub
			return mPrivateData;
		}
		
		public boolean addNode(USBNode node) {
			// TODO Auto-generated method stub
			return mList.add(node);
		}
	}
}
