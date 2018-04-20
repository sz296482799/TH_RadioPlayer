package com.taihua.th_radioplayer.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.storage.StorageManager;
import com.taihua.th_radioplayer.utils.UsbUtils;
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

	public USBNode getUSBFile(File f, String[] Extensions) {
	    return getUSBFile(f.getAbsoluteFile(), Extensions);
    }

    public USBNode getUSBFile(String path, String[] Extensions) {

        if(path != null) {
            File file = new File(path);
            if(file.exists()) {

                USBNode node = new USBNode();
                node.setName(file.getName());
                node.setPath(file.getAbsolutePath());
                node.setType(file.isDirectory() ? USBNode.NODE_TYPE_DIR : USBNode.NODE_TYPE_FILE);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (Extensions != null && Extensions.length > 0) {
                            for (String e : Extensions) {
                                if (e != null && f.getPath().substring(f.getPath().length() - e.length()).equals(e)) {
                                    node.addNode(getUSBFile(f, Extensions));
                                    break;
                                }
                            }
                        }
                        else
                            node.addNode(getUSBFile(f, Extensions));
                    }
                    if(node.size() == 0)
                        return null;
                    return node;
                }
                return node;
            }
        }
        return null;
    }
	
	public USBNode getRootFile(String[] Extensions) {

        List<String> paths = UsbUtils.getMountPathList();
        if(paths != null && paths.size() > 0) {
            USBNode node = new USBNode();
            node.setName("ROOT");
            node.setPath("");
            node.setType(USBNode.NODE_TYPE_DIR);

            for (String path : paths) {
                node.addNode(getUSBFile(path, Extensions));
            }
            return node;
        }
        return null;
	}
	
	static public class USBNode {

		public static final int NODE_TYPE_DIR = 0;
		public static final int NODE_TYPE_FILE = 1;
		
		private String mName = null;
		private int mType = -1;
		private ArrayList<USBNode> mList = null;
		private String mPath;
		
		public USBNode() {
			mName = "UNKNOW";
			mList = new ArrayList<USBNode>();
		}
		
		public void setName(String name) {
			mName = name;
		}
		
		public String getName() {
			return mName;
		}
		
		public void setType(int type) {
			mType = type;
		}
		
		public int getType() {
			return mType;
		}

        public void setPath(String path) {
            mPath = path;
        }

        public String getPath() {
            return mPath;
        }

        public int size() {
		    return mList.size();
        }

        public boolean addNode(USBNode node) {

            if(node == null)
                return false;
			return mList.add(node);
		}
	}
}
