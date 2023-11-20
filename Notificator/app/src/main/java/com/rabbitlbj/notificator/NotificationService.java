package com.rabbitlbj.notificator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    private static List<NotificationAdapter.NotificationItem> notificationList = new ArrayList<>();
    private static final String NOTIFICATION_CHANNEL = "my_channel_id";

    private Messenger activityMessenger;

    private Socket socket;
    @Override
    public void onCreate() {
        Log.d("AAAAAA", "onCreate");
        super.onCreate();

        Notification notification = buildNotification("I`m notificator", "Keep running...");
        startForeground(1, notification);

//        connectToServer("router.wxioi.fun", 8991);
        readFromSocket();
//        monitorSocket();
        restoreData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AAAAAA", "onStartCommand");
        if (intent != null) {
            // 获取从Activity传递过来的Messenger
            activityMessenger = intent.getParcelableExtra("messenger");
            sendAllListData();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("AAAAAA", "onDestroy");
        saveData();
        super.onDestroy();
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences("YourServicePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationList);
        editor.putString("data", json);
        editor.apply();
    }

    private void restoreData() {
        SharedPreferences preferences = getSharedPreferences("YourServicePrefs", Context.MODE_PRIVATE);
        String json = preferences.getString("data", null);
        if (json != null) {
            // 将 JSON 字符串转换为 List
            Gson gson = new Gson();
            Type listType = new TypeToken<List<NotificationAdapter.NotificationItem>>(){}.getType();
            notificationList = gson.fromJson(json, listType);
        }
    }

    private void addItemToList(String title, String content) {
        notificationList.add(NotificationAdapter.createItem(title, content));

        Message msg = Message.obtain(null, 1); // 1 is just an example message type
        msg.getData().putString("title", title);
        msg.getData().putString("content", content);
        sendMessageToActivity(msg);
    }

    private void sendAllListData() {
        Message msg = Message.obtain(null, 2); // 1 is just an example message type
        msg.obj = notificationList;
        sendMessageToActivity(msg);
    }

    private void sendMessageToActivity(Message msg) {
        if (activityMessenger != null) {
            try {
                // 发送消息给Activity
                activityMessenger.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToServer(String serverIp, int serverPort) {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int reconnectAttempts = 0;
        final int MAX_RECONNECT_ATTEMPTS = 5;
        while (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            try {
                //            socket = new Socket(serverIp, serverPort);
                SocketAddress socketAddress = new InetSocketAddress(serverIp, serverPort);
                socket = new Socket();
                socket.connect(socketAddress);
                socket.setKeepAlive(true); // 启用TCP Keep-Alive机制
                //             socket.setTcpKeepAlive(true); // 对于Android，需要设置这个选项
                //             socket.setSoTimeout(5); // 连接空闲超时时间
                //             socket.setSoLinger(true, 5); // 如果连接在空闲期间被关闭，则底层套接字会立即关闭

                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000); // 5秒后尝试重新连接
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            reconnectAttempts++;
        }
    }

    private void readFromSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        connectToServer("router.wxioi.fun", 8991);

                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            String message = new String(buffer, 0, bytesRead);
                            String[] parts = message.split("\u0001");
                            if(parts.length == 2) {
                                addMessage(parts[0], parts[1]);
                            } else {
                                addMessage("", parts[0]);
                            }
                        }
                    } catch (java.net.SocketException e) {
                        try {
                            connectToServer("router.wxioi.fun", 8991);

                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void monitorSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        boolean closed = socket.isClosed();
                        boolean connected = socket.isConnected();

                        Log.i("IIIIIII :  ", (closed ? "closed" : "") + "   " + (connected ? "connected" : ""));

                        if(closed) {

                        }

                        if(connected) {

                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).start();
    }

    void addMessage(String title, String content) {
        Log.d("TTTTT", title + "    " + content);
        showNotification(title, content);
        addItemToList(title, content);
        Log.d("TTTT", String.valueOf(notificationList.size()));
    }

    private void showNotification(String title, String content) {
        int notificationId = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, buildNotification(title, content));
    }

    private Notification buildNotification(String title, String content) {
        String channelId = NOTIFICATION_CHANNEL;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.hello)
                .setContentTitle(title).setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel";
            String channelDescription = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("AAAAAA", "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("AAAAAA", "onUnbind");
        return false;
    }
}