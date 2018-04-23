package com.taihua.th_radioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taihua.th_radioplayer.connection.MQTTConnection;
import com.taihua.th_radioplayer.connection.ServerConnection;
import com.taihua.th_radioplayer.database.PlayerDB;
import com.taihua.th_radioplayer.domain.*;
import com.taihua.th_radioplayer.global.Config;
import com.taihua.th_radioplayer.manager.BluetoothManager;
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
import com.taihua.th_radioplayer.utils.JasonUtils;
import com.taihua.th_radioplayer.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    public static final String TAG = "MAIN_SERVICE";

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
    private BluetoothManager mBluetoothManager;
    private MQTTConnection mMqttConnection;

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
            LogUtil.d(TAG, "setCurrentTimeMillis Time:" + returnTimeOB.getTime());
            SystemClock.setCurrentTimeMillis(returnTimeOB.getTime() * 1000);
        }
    }

    private Thread mClientThread = new Thread(new Runnable() {
        @Override
        public void run() {

            LogUtil.d(TAG, "ClientThread Start!");

            setSystemTime();

            mBaseDataUpdater.init();

            mCarouselTimer.init();
            mBroadcastTimer.init();

            mMqttConnection.init(mDeviceID, mCallback);

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

                case BluetoothManager.MESSAGE_DISCONNECTED:
                    System.out.println("disconnected");
                    break;
                case BluetoothManager.MESSAGE_READ:
                    System.out.println("message_read");
                    Bundle temp =  msg.getData();
                    byte[] buffer = temp.getByteArray(BluetoothManager.READ_MSG);
                    String str = new String(buffer);
                    LogUtil.d(TAG, "BluetoothManager.MESSAGE_READ Str:" + str);
                    break;
                case BluetoothManager.MESSAGE_WRITE:
                    System.out.println("message_write");
                    break;
                case BluetoothManager.MESSAGE_STATE_CHANGE:
                    System.out.println("state_change state:" + msg.arg1);
                    break;

                default:
                    return false;
            }
            return true;
        }
    });

    private MQTTConnection.ConnectionCallback mCallback = new MQTTConnection.ConnectionCallback() {
        @Override
        public String onAction(PublishItem item) {
            if(Config.Option.get_base_data.getName().equals(item.getOption())) {
                String data = item.getData();
                if(data == null) {
                    BaseDataIB baseDataIB = mPlayerDB.getBaseData();
                    if(baseDataIB != null)
                        return JasonUtils.object2JsonString(baseDataIB.getData().getBase());
                }
                else {
                    BaseDataDataBaseIB baseDataIB = JasonUtils.Jason2Object(data, BaseDataDataBaseIB.class);
                    if(baseDataIB != null) {
                        if(setBaseData(baseDataIB))
                            return reasonCodeString(200, "OK");
                        else
                            return reasonCodeString(201, "ERROR");
                    }
                }
            }
            else if (Config.Option.get_set_data.getName().equals(item.getOption())) {
                String data = item.getData();
                if(data == null) {
                    ReturnSetOB returnSetOB = mPlayerDB.getServerData();
                    if(returnSetOB != null)
                        return JasonUtils.object2JsonString(returnSetOB);
                }
                else {
                    ReturnSetOB returnSetOB = JasonUtils.Jason2Object(data, ReturnSetOB.class);
                    if(returnSetOB != null) {
                        List<ReturnSetDataBroadcastOB> broadcastList = returnSetOB.getData().getBroadcast();
                        if(broadcastList != null)
                            mBroadcastTimer.setBroadcastList(broadcastList);

                        List<ReturnSetDataCarouselOB> carouselList = returnSetOB.getData().getCarousel();
                        if(carouselList != null)
                            mCarouselTimer.setCarouselList(carouselList);

                        return reasonCodeString(200, "OK");
                    }
                }
            }
            else if (Config.Option.get_music_data.getName().equals(item.getOption())){
                String data = item.getData();
                if(data != null) {
                    JSONObject jsonObject = JSON.parseObject(data);
                    if(jsonObject != null) {
                        Integer channel_id = jsonObject.getInteger("channel_id");
                        if(channel_id == null) {
                            List<RadioChannel> list = mRadioManager.getChannelList();
                            return JasonUtils.object2JsonString(list);
                        }
                        else {
                            RadioChannel channel = mRadioManager.getChannelByID(channel_id);
                            if(channel != null) {
                                List<RadioItem> list = channel.getRadios();
                                return JasonUtils.object2JsonString(list);
                            }
                            else {
                                return reasonCodeString(203, "No find channel!");
                            }
                        }
                    }
                }
            }
            else if (Config.Option.get_play_action.getName().equals(item.getOption())) {
                String data = item.getData();
                if(data != null) {
                    JSONObject jsonObject = JSON.parseObject(data);
                    if(jsonObject != null) {
                        Integer action_id = jsonObject.getInteger("action_id");
                        if(action_id != null) {
                            int id = action_id;
                            switch (id) {
                                case UploadLogUpdater.ACTION_TYPE_STARTPLAY:
                                    play();
                                    break;
                                case UploadLogUpdater.ACTION_TYPE_STOPPLAY:
                                    stop();
                                    break;
                                case UploadLogUpdater.ACTION_TYPE_SWITCH:
                                    Integer packet_id = jsonObject.getInteger("packet_id");
                                    Integer channel_id = jsonObject.getInteger("channel_id");
                                    Integer radio_id = jsonObject.getInteger("radio_id");

                                    switchRadio(
                                            packet_id == null ? -1 : packet_id,
                                            channel_id == null ? -1 : channel_id,
                                            radio_id == null ? -1 : radio_id
                                            );
                                    break;
                                case UploadLogUpdater.ACTION_TYPE_VOLUP:
                                    vol_up();
                                    break;
                                case UploadLogUpdater.ACTION_TYPE_VOLDOWN:
                                    vol_down();
                                    break;
                                case UploadLogUpdater.ACTION_TYPE_BROADCAST:
                                    ReturnSetDataBroadcastOB broadcast = jsonObject.getObject("broadcast", ReturnSetDataBroadcastOB.class);
                                    if(broadcast != null) {
                                        mBroadcastTimer.touchBroadcast(broadcast);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            return reasonCodeString(202, "ERROR");
        }
    };

    private String reasonCodeString(int code, String msg) {
        Reason reason = new Reason();
        reason.setReasonCode(code);
        reason.setMsg(msg);
        return JasonUtils.object2JsonString(reason);
    }

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

    private void switchRadio(int packetID, int channelID, int radioID) {
        boolean isSuccess = true;

        try {
            if (packetID != -1)
                isSuccess |= mPlayer.switchPacket(packetID);
            if (channelID != -1)
                isSuccess |= mPlayer.switchChannel(channelID);
            if (radioID != -1)
                isSuccess |= mPlayer.switchRadio(radioID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isSuccess)
            play();
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

    private boolean setBaseData(BaseDataDataBaseIB basedate) {
        if(mPlayer == null || basedate == null)
            return false;

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
        return true;
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
        mBluetoothManager = BluetoothManager.getInstance();
        mMqttConnection = MQTTConnection.getInstance();

        mStorageManager.init(this);

        mPlayerDB.init(this);
        mRadioManager.init(mHandler);

        mClientThread.start();

        mBluetoothManager.registerHandler(mHandler);
        mBluetoothManager.start();
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
