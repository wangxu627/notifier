package com.rabbitlbj.notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private List<NotificationAdapter.NotificationItem> notificationList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private static final String NOTIFICATION_CHANNEL = "my_channel_id";


    private Messenger serviceMessenger;

    // Handler处理从Service传来的消息
    private Handler activityHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    addNotificationIntoList(bundle.getString("title"), bundle.getString("content"));
                    break;
                case 2:
                    List<NotificationAdapter.NotificationItem> listData = (List<NotificationAdapter.NotificationItem>)msg.obj;
                    refillNotificationList(listData);
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        notificationList.add(NotificationAdapter.createItem("Info", "Waiting for message..."));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationAdapter = new NotificationAdapter(notificationList);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(layoutManager);

//        connectToServer("router.wxioi.fun", 8991);

        // 绑定Service

        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("messenger", new Messenger(activityHandler));
        startService(intent);

//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void connectToServer(String serverIp, int serverPort) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(serverIp, serverPort);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            void addMessage(String title, String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNotification(title, content);
                        addNotificationIntoList(title, content);
                    }
                });
            }
        }).start();
    }

    private void addNotificationIntoList(String title, String content) {
        notificationList.add(NotificationAdapter.createItem(title, content));
        notificationAdapter.notifyDataSetChanged();
    }

    private void refillNotificationList(List<NotificationAdapter.NotificationItem> listData) {
        notificationList.clear();
        notificationList.addAll(listData);
        notificationAdapter.notifyDataSetChanged();
    }

    private void showNotification(String title, String content) {
        int notificationId = (int) System.currentTimeMillis();
        String channelId = NOTIFICATION_CHANNEL;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.hello).setContentTitle(title).setContentText(content).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel";
            String channelDescription = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}