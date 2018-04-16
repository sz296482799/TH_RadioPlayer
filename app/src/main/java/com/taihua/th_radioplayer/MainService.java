package com.taihua.th_radioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import com.taihua.th_radioplayer.connection.ServerConnection;
import com.taihua.th_radioplayer.database.PlayerDB;
import com.taihua.th_radioplayer.domain.*;
import com.taihua.th_radioplayer.global.Config;
import com.taihua.th_radioplayer.manager.RadioManager;
import com.taihua.th_radioplayer.manager.RadioStorageManager;
import com.taihua.th_radioplayer.player.RadioChannel;
import com.taihua.th_radioplayer.player.RadioItem;
import com.taihua.th_radioplayer.player.RadioList;
import com.taihua.th_radioplayer.player.RadioPlayer;
import com.taihua.th_radioplayer.timer.BroadcastTimer;
import com.taihua.th_radioplayer.timer.CarouselTimer;
import com.taihua.th_radioplayer.update.BaseDataUpdater;
import com.taihua.th_radioplayer.update.UploadLogUpdater;
import com.taihua.th_radioplayer.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    public static final String TAG = "MAIN_SERVICE";

    public static final int PLAYER_ACTION_START = 0x2001;
    public static final int PLAYER_ACTION_STOP = 0x2002;
    public static final int PLAYER_ACTION_SWITCH= 0x2003;
    public static final int PLAYER_ACTION_VOLUP = 0x2004;
    public static final int PLAYER_ACTION_VOLDOWN = 0x2005;
    public static final int PLAYER_ACTION_VOLSET = 0x2006;

    private String mDeviceID = null;
    private RadioPlayer mPlayer = null;
    private CarouselTimer mCarouselTimer = null;
    private BroadcastTimer mBroadcastTimer = null;
    private BaseDataUpdater mBaseDataUpdater = null;
    private UploadLogUpdater mUploadLogUpdater = null;
    private RadioManager mRadioManager = null;
    private RadioStorageManager mStorageManager = null;
    private PlayerDB mPlayerDB = null;
    private AudioManager mAudioManager;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
    }

    private void setSystemTime() {
        ReturnTimeOB returnTimeOB = ServerConnection.getInstance().getServerTime();
        if(returnTimeOB != null && returnTimeOB.getResponse_code() == 1) {
            //SystemClock.setCurrentTimeMillis(returnTimeOB.getTime());
        }
    }

    private Thread mClientThread = new Thread(new Runnable() {
        @Override
        public void run() {

            setSystemTime();

            mBaseDataUpdater.init();

            mCarouselTimer.init();
            mBroadcastTimer.init();

            ReturnOB returnOB = mPlayerDB.getKeyData();
            if(returnOB == null) {
                InitclientIB initclientIB = new InitclientIB(mDeviceID, Config.CLINE_TYPE);
                returnOB = ServerConnection.getInstance().setClientInit(initclientIB);
            }

            if(returnOB != null && returnOB.getResponse_code() == 1) {

                ServerConnection.getInstance().setClientSecret("3affc105c803e49da18849b3ccb1c96a");//returnOB.getClient_secret()
                mBaseDataUpdater.start();
                mUploadLogUpdater.start();

                mPlayerDB.writeKeyData(returnOB);
            }
        }
    });

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "Handler:" + msg);
            switch (msg.what) {

                case BaseDataUpdater.UPDATE_BASE_DATA:
                    BaseDataDataBaseIB baseDataIB = (BaseDataDataBaseIB)msg.obj;
                    if(baseDataIB != null) {
                        setBaseData(baseDataIB);
                    }
                    break;

                case BaseDataUpdater.UPDATE_RADIO_PACKET_LIST:
                    @SuppressWarnings("unchecked")
                    ArrayList<BaseDataDataIteamIB> packetList = (ArrayList<BaseDataDataIteamIB>)msg.obj;
                    if(packetList == null)
                        break;

                    RadioList list = mPlayer.getPlayerList();
                    if(list == null) {
                        list = new RadioList();
                        mPlayer.setPlayerList(list);
                    }

                    mRadioManager.clearPacket();
                    for(BaseDataDataIteamIB p : packetList) {

                        mRadioManager.updatePacket(p);
                    }
                    mRadioManager.bindChannel();
                    mRadioManager.clearUnusedChannel();
                    mRadioManager.updateDB();
                    break;

                case BaseDataUpdater.UPDATE_RADIO_CHANNEL_LIST:
                    ReturnMusicOB returnMusicOB = (ReturnMusicOB) msg.obj;
                    if(returnMusicOB != null)
                        setMusic(returnMusicOB);
                    break;

                case BaseDataUpdater.UPDATE_RADIO_CHANNEL_FINISH:
                    mRadioManager.bindChannel();
                    mRadioManager.clearUnusedChannel();
                    mRadioManager.updateDB();
                    break;

                case BaseDataUpdater.UPDATE_SET_BROADCAST:
                    mBroadcastTimer.setBroadcastList((List<ReturnSetDataBroadcastOB>) msg.obj);
                    break;
                case BaseDataUpdater.UPDATE_SET_CAROUSEL:
                    mCarouselTimer.setCarouselList((List<ReturnSetDataCarouselOB>) msg.obj);
                    break;

                case BroadcastTimer.BROADCAST_TIMER_MSG:
                    mPlayer.setBroadcast((BroadcastTimer.BroadcastItem) msg.obj);
                    addAction(UploadLogUpdater.ACTION_TYPE_BROADCAST);
                    break;

                case CarouselTimer.CAROUSEL_TIMER_MSG:
                    mPlayer.setCarousel((CarouselTimer.CarouselItem) msg.obj);
                    addAction(UploadLogUpdater.ACTION_TYPE_CAROUSEL);
                    break;

                case RadioManager.RADIO_DOWNLOAD_START:
                    mUploadLogUpdater.addUpdate((RadioChannel) msg.obj, UploadLogUpdater.UPADTE_TYPE_START);
                    break;

                case RadioManager.RADIO_DOWNLOAD_END:
                    RadioChannel channel = (RadioChannel) msg.obj;
                    if(channel != null)
                        mUploadLogUpdater.addUpdate(channel,
                                channel.getDownloadFailNum() > 0 ? UploadLogUpdater.UPADTE_TYPE_FAIL : UploadLogUpdater.UPADTE_TYPE_SUCC);
                    break;

                case PLAYER_ACTION_START:
                    play();
                    break;
                case PLAYER_ACTION_STOP:
                    stop();
                    break;
                case PLAYER_ACTION_SWITCH:
                    switchChannel(msg.arg1, msg.arg2);
                    break;
                case PLAYER_ACTION_VOLUP:
                    vol_up();
                    break;
                case PLAYER_ACTION_VOLDOWN:
                    vol_down();
                    break;

                default:
                    return false;
            }
            return true;
        }
    });

    private void addAction(int actionID) {
        RadioItem radio = mPlayer.getPlayRadio();
        if(radio != null)
            mUploadLogUpdater.addAction(radio.getChannelID(), actionID, mPlayer.getActionType());
    }

    private void play() {
        try {
            mPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addAction(UploadLogUpdater.ACTION_TYPE_STARTPLAY);
    }

    private void stop() {
        mPlayer.stop();
        addAction(UploadLogUpdater.ACTION_TYPE_STOPPLAY);
    }

    private void switchChannel(int packetID, int channelID) {
        try {
            mPlayer.switchPacket(packetID, channelID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addAction(UploadLogUpdater.ACTION_TYPE_SWITCH);
    }

    private void vol_up() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI);
        addAction(UploadLogUpdater.ACTION_TYPE_VOLUP);
    }

    private void vol_down() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI);
        addAction(UploadLogUpdater.ACTION_TYPE_VOLDOWN);
    }

    private void setBaseData(BaseDataDataBaseIB basedate) {
        if(mPlayer == null || basedate == null)
            return;

        boolean isBroadcast = basedate.getIs_broadcast() == 1;
        boolean isCarousel = basedate.getIs_carousel() == 1;

        if(!isCarousel) {
            mPlayer.setCarousel(null);
            mCarouselTimer.stop();
        }
        else {
            mCarouselTimer.start();
        }

        if(!isBroadcast) {
            mPlayer.setBroadcast(null);
            mBroadcastTimer.stop();
        }
        else {
            mBroadcastTimer.start();
        }
        mBaseDataUpdater.setCycle(basedate.getBox_check_cycle());
        mUploadLogUpdater.setCycle(basedate.getBox_log_cycle());
        mUploadLogUpdater.setIsUploadAction(basedate.getIs_action_log_upload() == 1);
        mUploadLogUpdater.setIsUploadUpdate(basedate.getIs_update_log_upload() == 1);
    }

    private void setMusic(ReturnMusicOB returnMusicOB) {

        if(returnMusicOB == null)
            return;

        List<ReturnMusicChannelOB> data = returnMusicOB.getData();

        for(ReturnMusicChannelOB c : data) {
            mRadioManager.updateChannel(c);
        }
    }

    private void serviceInit()
    {
        mDeviceID = "aabbccdd";
        mStorageManager = RadioStorageManager.getInstance();
        mPlayer = new RadioPlayer(new RadioList());
        mCarouselTimer = new CarouselTimer(mHandler);
        mBroadcastTimer = new BroadcastTimer(mHandler);
        mBaseDataUpdater = new BaseDataUpdater(mDeviceID, mHandler);
        mUploadLogUpdater = new UploadLogUpdater(mDeviceID);
        mRadioManager = RadioManager.getInstance();
        mPlayerDB = PlayerDB.getInstance();
        mAudioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);

        mStorageManager.init(this);

        mPlayerDB.init(this);
        mRadioManager.init(mHandler);

        mClientThread.start();
    }



    public int onStartCommand(Intent intent, int flags, int startId)
    {
        serviceInit();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
}
