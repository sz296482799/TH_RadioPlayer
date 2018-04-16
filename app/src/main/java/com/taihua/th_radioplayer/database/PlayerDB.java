package com.taihua.th_radioplayer.database;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.alibaba.fastjson.TypeReference;
import com.taihua.th_radioplayer.domain.BaseDataIB;
import com.taihua.th_radioplayer.domain.ReturnOB;
import com.taihua.th_radioplayer.domain.ReturnSetOB;
import com.taihua.th_radioplayer.player.RadioChannel;
import com.taihua.th_radioplayer.utils.JasonUtils;
import com.taihua.th_radioplayer.utils.LogUtil;

public class PlayerDB {

	public static final String TAG = "PLAYER_DB";
	public static final int DB_VERSION = 1;
    public static final int KEY_DB_VERSION = 1;
	
	private static PlayerDB mInstance;
	private DBHelper mDBHelper = null;
    private DBHelper mKeyDBHelper = null;
	
	private PlayerDB() {
		// TODO Auto-generated constructor stub
	}

	public static PlayerDB getInstance() {

		if (mInstance == null) {
			synchronized (PlayerDB.class) {
				if (mInstance == null) {
					mInstance = new PlayerDB();
				}
			}
		}
		return mInstance;
	}

	public void init(Context context) {

	    mDBHelper = new DBHelper(context, "radio.db", null, DB_VERSION);
        mKeyDBHelper = new DBHelper(context, "key.db", null, KEY_DB_VERSION);
	}

    public boolean writeKeyData(ReturnOB returnOB) {
        if(mDBHelper != null) {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.JASON_TYPE_KEY, DBHelper.JASON_TYPE_KEYDATA);
            values.put(DBHelper.JASON_VALUE_KEY, JasonUtils.object2JsonString(returnOB));

            db.delete(DBHelper.RADIO_TABLE, DBHelper.JASON_TYPE_KEY + "=?", new String[]{"" + DBHelper.JASON_TYPE_KEYDATA});
            db.insert(DBHelper.RADIO_TABLE, null, values);
            return true;
        }
        return false;
    }

    public ReturnOB getKeyData() {
        if(mDBHelper != null) {
            SQLiteDatabase db = mDBHelper.getWritableDatabase();

            Cursor cursor = db.query(DBHelper.RADIO_TABLE, new String[] { DBHelper.JASON_VALUE_KEY }, DBHelper.JASON_TYPE_KEY + "=?", new String[] { "" + DBHelper.JASON_TYPE_KEYDATA }, null, null, null);

            while (cursor.moveToNext()) {
                String jasonStr = cursor.getString(cursor.getColumnIndex(DBHelper.JASON_VALUE_KEY));
                LogUtil.d(TAG, "getKeyData:" + jasonStr);
                return JasonUtils.Jason2Object(jasonStr, ReturnOB.class);
            }
        }
        return null;
    }
	
	public boolean writeBaseData(BaseDataIB baseData) {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBHelper.JASON_TYPE_KEY, DBHelper.JASON_TYPE_BASEDATA);
			values.put(DBHelper.JASON_VALUE_KEY, JasonUtils.object2JsonString(baseData));
			
			db.delete(DBHelper.RADIO_TABLE, DBHelper.JASON_TYPE_KEY + "=?", new String[]{"" + DBHelper.JASON_TYPE_BASEDATA});
			db.insert(DBHelper.RADIO_TABLE, null, values);
			return true;
		}
		return false;
	}
	
	public BaseDataIB getBaseData() {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();

			Cursor cursor = db.query(DBHelper.RADIO_TABLE, new String[] { DBHelper.JASON_VALUE_KEY }, DBHelper.JASON_TYPE_KEY + "=?", new String[] { "" + DBHelper.JASON_TYPE_BASEDATA }, null, null, null);

			while (cursor.moveToNext()) {
                String jasonStr = cursor.getString(cursor.getColumnIndex(DBHelper.JASON_VALUE_KEY));
                LogUtil.d(TAG, "getBaseData:" + jasonStr);
                return JasonUtils.Jason2Object(jasonStr, BaseDataIB.class);
            }
		}
		return null;
	}
	
	public boolean writeServerData(ReturnSetOB returnSetOB) {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBHelper.JASON_TYPE_KEY, DBHelper.JASON_TYPE_SERVERDATA);
			values.put(DBHelper.JASON_VALUE_KEY, JasonUtils.object2JsonString(returnSetOB));
			
			db.delete(DBHelper.RADIO_TABLE, DBHelper.JASON_TYPE_KEY + "=?", new String[]{"" + DBHelper.JASON_TYPE_SERVERDATA});
			db.insert(DBHelper.RADIO_TABLE, null, values);
			return true;
		}
		return false;
	}
	
	public ReturnSetOB getServerData() {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();

			Cursor cursor = db.query(DBHelper.RADIO_TABLE, new String[] { DBHelper.JASON_VALUE_KEY }, DBHelper.JASON_TYPE_KEY + "=?", new String[] { "" + DBHelper.JASON_TYPE_SERVERDATA }, null, null, null);

			while (cursor.moveToNext()) {
                String jasonStr = cursor.getString(cursor.getColumnIndex(DBHelper.JASON_VALUE_KEY));
                LogUtil.d(TAG, "getServerData:" + jasonStr);
                return JasonUtils.Jason2Object(jasonStr, ReturnSetOB.class);
            }
		}
		return null;
	}
	
	public boolean writeChannelData(HashMap<Integer, RadioChannel> channelList) {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(DBHelper.JASON_TYPE_KEY, DBHelper.JASON_TYPE_CHANNELDATA);
			String jsonStr = JasonUtils.object2JsonString(channelList);
            LogUtil.d(TAG, "writeChannelData jsonStr:" + jsonStr);
			values.put(DBHelper.JASON_VALUE_KEY, jsonStr);

			db.delete(DBHelper.RADIO_TABLE, DBHelper.JASON_TYPE_KEY + "=?", new String[]{"" + DBHelper.JASON_TYPE_CHANNELDATA});
			db.insert(DBHelper.RADIO_TABLE, null, values);
		}
		return false;
	}
	
	public HashMap<Integer, RadioChannel> getChannelData() {
		if(mDBHelper != null) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();

			Cursor cursor = db.query(DBHelper.RADIO_TABLE, new String[] { DBHelper.JASON_VALUE_KEY }, DBHelper.JASON_TYPE_KEY + "=?", new String[] { "" + DBHelper.JASON_TYPE_CHANNELDATA }, null, null, null);

			while (cursor.moveToNext()) {
                String jasonStr = cursor.getString(cursor.getColumnIndex(DBHelper.JASON_VALUE_KEY));
                LogUtil.d(TAG, "getChannelData:" + jasonStr);
                return JasonUtils.Jason2Object(jasonStr, new TypeReference<HashMap<Integer, RadioChannel>>(){});
            }
		}
		return null;
	}
	
	private class DBHelper extends SQLiteOpenHelper {

		public static final String RADIO_TABLE = "RadioDBTable";
		
		public static final int JASON_TYPE_BASEDATA = 1;
		public static final int JASON_TYPE_SERVERDATA = 2;
		public static final int JASON_TYPE_CHANNELDATA = 3;

        public static final int JASON_TYPE_KEYDATA = 0x1001;
		
		public static final String JASON_TYPE_KEY = "JASON_TYPE";
		public static final String JASON_VALUE_KEY = "JASON_VALUE";
		
		private static final String CREATE_RADIO_TABLE = "CREATE TABLE " + RADIO_TABLE
			    + "("
			    + JASON_TYPE_KEY + " INTEGER, "
			    + JASON_VALUE_KEY + " TEXT "
			    + ")";
		
		/*private static final String CLIENTID_KEY = "CLIENT_ID";
		private static final String CLIENTCODE_KEY = "CLIENT_CODE";
		private static final String HW_NUM_KEY = "HARDWARE_NUM";
		private static final String IS_CAROUSEL_KEY = "IS_CAROUSEL";
		private static final String IS_EDIT_CAROUSE_KEY = "IS_EDIT_CAROUSE";
		private static final String IS_BROADCASET_KEY = "IS_BROADCASET";
		private static final String IS_EDIT_BROADCASET_KEY = "IS_EDIT_BROADCASET";
		private static final String IS_ACTION_LOG_KEY = "IS_ACTION_LOG";
		private static final String IS_UPDATE_LOG_KEY = "IS_UPDATE_LOG";
		private static final String LOG_CYCLE_KEY = "BOX_LOG_CYCLE";
		private static final String CHECK_CYCLE_KEY = "BOX_CHECK_CYCLE";
		
	    private static final String CREATE_BASE_TABLE = "CREATE TABLE " + BASE_TABLE
	    + "(" 
	    + CLIENTCODE_KEY + " TEXT, "
	    + HW_NUM_KEY + " TEXT, "
	    + CLIENTID_KEY + " INTEGER, "
	    + IS_CAROUSEL_KEY + " INTEGER, "
	    + IS_EDIT_CAROUSE_KEY + " INTEGER, "
	    + IS_BROADCASET_KEY + " INTEGER, "
	    + IS_EDIT_BROADCASET_KEY + " INTEGER, "
	    + IS_ACTION_LOG_KEY + " INTEGER, "
	    + IS_UPDATE_LOG_KEY + " INTEGER, "
	    + LOG_CYCLE_KEY + " INTEGER, "
	    + CHECK_CYCLE_KEY + " INTEGER, "
	    + ")";*/
		
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}
	    
	    private void dropTable(SQLiteDatabase db) {
			// TODO Auto-generated method stub
	    	db.execSQL("DROP TABLE IF EXISTS " + RADIO_TABLE);
		}
	    
	    private void createTable(SQLiteDatabase db) {
			// TODO Auto-generated method stub
	    	db.execSQL(CREATE_RADIO_TABLE);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			createTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			dropTable(db);
			createTable(db);
		}
		
	}
}
