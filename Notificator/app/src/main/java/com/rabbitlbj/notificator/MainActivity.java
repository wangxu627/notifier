package com.rabbitlbj.notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
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

        connectToServer("router.wxioi.fun", 8991);
//        connectToServer("10.196.10.21", 8991);
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