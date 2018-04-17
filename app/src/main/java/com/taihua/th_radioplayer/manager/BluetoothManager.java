package com.taihua.th_radioplayer.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.taihua.th_radioplayer.utils.LogUtil;

public class BluetoothManager {

    private static final String TAG = "BluetoothManager";

    private static final String NAME = "BluetoothManager";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mAdapter;
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private static BluetoothManager mInstance;
    private BluetoothDevice mConnectedBluetoothDevice;

    private static final int STATE_NONE = 0x4000;
    private static final int STATE_LISTEN = 0x4001;
    private static final int STATE_CONNECTING = 0x4002;
    private static final int STATE_CONNECTED = 0x4003;
    private static final int STATAE_CONNECT_FAILURE = 0x4004;

    public static final int MESSAGE_DISCONNECTED = 0x4005;
    public static final int MESSAGE_STATE_CHANGE = 0x4006;
    public static final int MESSAGE_READ = 0x4007;
    public static final int MESSAGE_WRITE= 0x4008;

    public static final String DEVICE_NAME = "device_name";
    public static final String READ_MSG = "read_msg";

    private BluetoothManager() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mAdapter != null) {
            if(!mAdapter.isEnabled())
                mAdapter.enable();
        }
        mState = STATE_NONE;
    }

    public static BluetoothManager getInstance() {

        if (mInstance == null) {
            synchronized (BluetoothManager.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothManager();
                }
            }
        }
        return mInstance;
    }

    private synchronized void setState(int state) {
        LogUtil.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    private boolean checkHasBluetooth() {
        if(mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                return mAdapter.enable();
            }
            return true;
        }
        LogUtil.d(TAG, "No Bluetooth!");
        return false;
    }

    public void registerHandler(Handler handler){
        mHandler = handler;
    }

    public void unregisterHandler(){
        mHandler = null;
    }

    public synchronized int getState() {
        return mState;
    }

    public BluetoothDevice getConnectedDevice(){
        return mConnectedBluetoothDevice;
    }

    public synchronized void start() {
        LogUtil.d(TAG, "start");

        if(!checkHasBluetooth())
            return;

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        LogUtil.d(TAG, "connect to: " + device);

        if(!checkHasBluetooth())
            return;

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        LogUtil.d(TAG, "connected");

        if(!checkHasBluetooth())
            return;

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        mConnectedBluetoothDevice =  device;
        Message msg = mHandler.obtainMessage(STATE_CONNECTED);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        LogUtil.d(TAG, "stop");

        if(!checkHasBluetooth())
            return;

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    public void write(byte[] out) {

        if(!checkHasBluetooth())
            return;

        ConnectedThread r;

        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);

        Message msg = mHandler.obtainMessage(STATAE_CONNECT_FAILURE);
        mHandler.sendMessage(msg);
        mConnectedBluetoothDevice = null;
    }

    private void connectionLost() {
        setState(STATE_LISTEN);

        Message msg = mHandler.obtainMessage(MESSAGE_DISCONNECTED);
        mHandler.sendMessage(msg);
        mConnectedBluetoothDevice = null;
        stop();
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mServerSocket;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);

            } catch (IOException e) {
                LogUtil.e(TAG, "listen() failed! error:" + e);
            }
            mServerSocket = tmp;
        }

        public void run() {
            LogUtil.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            if(mServerSocket == null) {
                return;
            }

            while (mState != STATE_CONNECTED) {
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    LogUtil.e(TAG, "accept() failed! error:" + e);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothManager.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:

                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    LogUtil.e(TAG, "Could not close unwanted socket! error:" + e);
                                }
                                break;
                        }
                    }
                }
            }
            LogUtil.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            LogUtil.d(TAG, "cancel " + this);
            try {
                mServerSocket.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "close() of server failed! error:" + e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;

            try {
                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                LogUtil.e(TAG, "create() failed! error:" + e);
                mmSocket = null;
            }
        }

        public void run() {
            LogUtil.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            mAdapter.cancelDiscovery();

            try {

                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();

                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    LogUtil.e(TAG, "unable to close() socket during connection failure! error:" +  e2);
                }

                BluetoothManager.this.start();
                return;
            }

            synchronized (BluetoothManager.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "close() of connect socket failed! error:" + e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            LogUtil.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                LogUtil.e(TAG, "没有创建临时sockets error:" + e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            LogUtil.i(TAG, "BEGIN mConnectedThread");
            int bytes;

            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);

                    Message msg = mHandler.obtainMessage(MESSAGE_READ);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(READ_MSG, buffer);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    LogUtil.e(TAG, "disconnected error:" + e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                LogUtil.e(TAG, "Exception during write! error:" + e);
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LogUtil.e(TAG, "close() of connect socket failed! error:" + e);
            }
        }
    }
}