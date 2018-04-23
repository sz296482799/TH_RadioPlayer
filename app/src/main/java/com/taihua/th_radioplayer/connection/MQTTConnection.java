package com.taihua.th_radioplayer.connection;

import com.alibaba.fastjson.JSON;
import com.taihua.th_radioplayer.domain.Connected;
import com.taihua.th_radioplayer.domain.DeviceID;
import com.taihua.th_radioplayer.domain.PublishItem;
import com.taihua.th_radioplayer.global.Config;
import com.taihua.th_radioplayer.utils.LogUtil;
import com.taihua.th_radioplayer.utils.MD5Util;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Date;

public class MQTTConnection implements MqttCallback {

    public static final String TAG = "MQTTConnection";

	private static MQTTConnection mInstance;
    private String mClientID;
    private long mClientTime = 0;

    private MqttThread mMqttThread;
    private final static short KEEP_ALIVE = 30;

    private ConnectionCallback mCallback;

	private MQTTConnection() {
	}

	public static MQTTConnection getInstance() {
	    if (mInstance == null) {
	        synchronized (MQTTConnection.class) {
	            if(mInstance == null) {
	                mInstance = new MQTTConnection();
                }
            }
        }
        return mInstance;
    }

    public void init(String deviceID, ConnectionCallback cb) {

        mCallback = cb;

        mMqttThread = new MqttThread(deviceID, this);
        mMqttThread.start();
    }

    public void start() {
	    if(mMqttThread != null) {

            mMqttThread.connect(true, KEEP_ALIVE);
            clientSubscribe(mMqttThread.mClientID);
        }
    }

    public void stop() {
        if(mMqttThread != null) {
            mMqttThread.disconect();
            clientUnsubscribe(mMqttThread.mClientID);
        }
    }

    private void clientSubscribe(String clientID) {
	    String[] topicName = {clientID};
	    int[] quality = {0};
        mMqttThread.subscribe(topicName, quality);
    }

    private void clientUnsubscribe(String clientID) {
        String[] topicName = {clientID};
        int[] quality = {0};
        mMqttThread.unsubscribe(topicName, quality);
    }

    private String getToken(String option, String id) {
        if(id != null && mClientTime > 0) {
            return MD5Util.MD5(option + mClientTime + id);
        }
        return null;
    }

    private synchronized boolean publishSecret(String option, Object obj) {

        return publishSecret(option, obj == null ? null : JSON.toJSONString(obj));
    }

    private synchronized boolean publishSecret(String option, String dataStr) {

        if(mClientID == null || mClientTime <= 0) {
            LogUtil.e(TAG,"publishSecret param error!");
            return false;
        }

        PublishItem item = new PublishItem();

        item.setOption(option);
        item.setToken(getToken(option, mMqttThread.getDeviceID()));
        if (dataStr != null)
            item.setData(dataStr);

        LogUtil.d(TAG,"publishSecret:" + JSON.toJSONString(item));
        mMqttThread.publish(mClientID, JSON.toJSONString(item));
        return true;
    }

    private boolean publishConnected() {
        Connected connected = new Connected();

        mClientTime = new Date().getTime();
        connected.setCode(200);
        connected.setDate(mClientTime);

        return publishSecret(Config.Option.connected.getName(), connected);
    }

    private boolean publishDisconnected() {
        mClientTime = 0;
        return publishSecret(Config.Option.disconnected.getName(), null);
    }

    private boolean checkToken(String option, String token) {
	    return option != null && token != null && token.equals(getToken(option, mClientID));
    }

    private void parse(PublishItem item) throws MqttException {

        if(Config.Option.connected.getName().equals(item.getOption())) {
            DeviceID data = JSON.parseObject(item.getData(), DeviceID.class);
            LogUtil.d(TAG,"DeviceID:" + data);
            if(data != null) {
                LogUtil.d(TAG,"device_id:" + data.getDeviceid());
                clientConnect(data.getDeviceid());
            }
        }
        else if(checkToken(item.getOption(), item.getToken())) {
            if(Config.Option.disconnected.getName().equals(item.getOption())) {
                clientDisconnect();
            }
            else if(Config.Option.get_base_data.getName().equals(item.getOption())
                    || Config.Option.get_set_data.getName().equals(item.getOption())
                    || Config.Option.get_music_data.getName().equals(item.getOption())
                    || Config.Option.get_play_action.getName().equals(item.getOption())
                    ) {
                if(mCallback != null) {
                    String publishStr = mCallback.onAction(item);
                    if(publishStr != null) {
                        publishSecret(item.getOption(), publishStr);
                    }
                }
            }
        }
        else {
            LogUtil.e(TAG,"Check Error! token:" + getToken(item.getOption(), mClientID) + " new token" + item.getToken());
        }
    }

    private synchronized void clientDisconnect() throws MqttException {
        publishDisconnected();
        mClientID = null;
    }

    private synchronized void clientConnect(String clientID) throws MqttException {

        if( !isConnected() || clientID == null) {
            LogUtil.e(TAG,"no Connected or param error!");
            return;
        }

        if(mClientID != null) {
            if (!mClientID.equals(clientID))
                clientDisconnect();
            else {
                LogUtil.e(TAG,"Already Connect!");
                return;
            }
        }
        mClientID = clientID;
        publishConnected();
    }

    public boolean isConnected() {
		return mMqttThread.isConnected();
	}

	public boolean waitConnect(long waitTime) throws InterruptedException {
	    long start = System.currentTimeMillis();
	    while (!isConnected() && (waitTime <= 0 || start + waitTime > System.currentTimeMillis())) {
	        Thread.sleep(100);
        }
        return isConnected();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LogUtil.e(TAG,"Lost! throwable:" + throwable);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String publishStr = new String(mqttMessage.getPayload());
        LogUtil.d(TAG,"s:" + s);
        LogUtil.d(TAG,"mDeviceID:" + mMqttThread.getDeviceID());
        LogUtil.d(TAG,"b:" + mqttMessage.isRetained());
        LogUtil.d(TAG,"publishArrived:" + publishStr);
        if(s != null && s.equals(mMqttThread.getDeviceID()) && !mqttMessage.isRetained()) {
            PublishItem item = JSON.parseObject(publishStr, PublishItem.class);
            if(item != null) {
                LogUtil.d(TAG,"PublishItem:" + item);
                parse(item);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LogUtil.d(TAG,"deliveryComplete:" + iMqttDeliveryToken.isComplete());
    }

    private class MqttThread implements Runnable {

	    private static final int MSG_CONNECT = 0;
        private static final int MSG_DISCONNECT = 1;
        private static final int MSG_PUBLISH = 2;
        private static final int MSG_SUBSCRIBE = 3;
        private static final int MSG_UNSUBSCRIBE = 4;

        private String mClientID;
        private MemoryPersistence mPersistence;
        private MqttClient mClient = null;
        private Thread mThread;
        private ArrayList<MqttMsg> mMsgList;
        private boolean isWaitConnect = false;

        public MqttThread(String clientID, MqttCallback cb) {
            try {
                mClientID = clientID;

                mPersistence = new MemoryPersistence();
                mClient = new MqttClient(Config.MQTT_SERVER_URL, clientID, mPersistence);
                mClient.setCallback(cb);

                mThread = new Thread(this);
                mMsgList = new ArrayList<MqttMsg>();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        private void _connect(boolean isCleanStart, short keepLive) throws MqttException {

            if(mClient != null) {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(isCleanStart);
                connOpts.setKeepAliveInterval(keepLive);
                connOpts.setUserName("admin");
                connOpts.setPassword("password".toCharArray());
                LogUtil.d(TAG,"Connecting to broker: " + mClient.getServerURI());
                mClient.connect(connOpts);
                LogUtil.d(TAG,"Connected");
                isWaitConnect = true;
            }
        }

        private void _disconect() throws MqttException {

            if(!mClient.isConnected())
                return;
            mClient.disconnect();
            isWaitConnect = false;
        }

        private void _publish(String topicName, String message, int quality, boolean isRetained) throws MqttException {

            if(!mClient.isConnected())
                return;

            mClient.publish(topicName,
                    message.getBytes(),
                    quality,
                    isRetained);
        }

        private void _subscribe(String[] topicName, int[] quality) throws MqttException {

            if(!mClient.isConnected())
                return;
            mClient.subscribe(topicName, quality);
        }

        private void _unsubscribe(String[] topicName) throws MqttException {

            if(!mClient.isConnected())
                return;
            mClient.unsubscribe(topicName);
        }

        private synchronized boolean putMsg(MqttMsg msg) {
            return mMsgList.add(msg);
        }

        private synchronized MqttMsg getMsg() {
            if(mMsgList.size() > 0)
                return mMsgList.remove(0);
            return null;
        }

        public void start() {
            mThread.start();
        }

        public String getDeviceID() {
            return mClientID;
        }

        public boolean isConnected() {
            return mClient.isConnected();
        }

        public void connect(boolean isCleanStart, short keepLive) {
            MqttMsg msg = new MqttMsg();
            msg.what = MSG_CONNECT;
            msg.isCleanStart = isCleanStart;
            msg.keepLive = keepLive;
            putMsg(msg);
        }

        public void disconect() {
            MqttMsg msg = new MqttMsg();
            msg.what = MSG_DISCONNECT;
            putMsg(msg);
        }

        public void publish(String topicName, String message) {
            MqttMsg msg = new MqttMsg();
            msg.what = MSG_PUBLISH;
            msg.topicName = new String[] {topicName};
            msg.message = message;
            putMsg(msg);
        }

        public void subscribe(String[] topicName, int[] quality) {
            MqttMsg msg = new MqttMsg();
            msg.what = MSG_SUBSCRIBE;
            msg.topicName = topicName;
            msg.quality = quality;
            putMsg(msg);
        }

        public void unsubscribe(String[] topicName, int[] quality) {
            MqttMsg msg = new MqttMsg();
            msg.what = MSG_UNSUBSCRIBE;
            msg.topicName = topicName;
            msg.quality = quality;
            putMsg(msg);
        }

        @Override
        public void run() {
            while (true) {
                MqttMsg msg = getMsg();
                if(msg != null) {
                    try {
                        switch (msg.what) {
                            case MSG_CONNECT:
                                if(!isConnected()) {
                                    LogUtil.d(TAG, "MSG_CONNECT!");
                                    _connect(msg.isCleanStart, msg.keepLive);
                                    isWaitConnect = false;
                                }
                                break;
                            case MSG_DISCONNECT:
                                LogUtil.d(TAG,"MSG_DISCONNECT!");
                                _disconect();
                                break;
                            case MSG_PUBLISH:
                                LogUtil.d(TAG,"MSG_PUBLISH!");
                                _publish(msg.topicName[0], msg.message, 0, false);
                                break;
                            case MSG_SUBSCRIBE:
                                LogUtil.d(TAG,"MSG_SUBSCRIBE!");
                                _subscribe(msg.topicName, msg.quality);
                                break;
                            case MSG_UNSUBSCRIBE:
                                LogUtil.d(TAG,"MSG_UNSUBSCRIBE!");
                                _unsubscribe(msg.topicName);
                                break;
                        }
                    } catch (MqttException me) {
                        LogUtil.e(TAG, "MqttThread MqttException:" + me);
                        if(msg.what == MSG_CONNECT && !mClient.isConnected() && isWaitConnect) {
                            LogUtil.e(TAG, "can't connect! retry after 30s!");
                            try {
                                Thread.sleep(30 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            putMsg(msg);
                        }
                    }
                }
            }
        }
    }

    private class MqttMsg {
	    int what;
	    boolean isCleanStart;
        short keepLive;
        String message;
        String[] topicName;
        int[] quality;
    }

    public interface ConnectionCallback {
	    String onAction(PublishItem item);
    }
}
