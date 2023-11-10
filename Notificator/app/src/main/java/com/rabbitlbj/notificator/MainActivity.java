package com.rabbitlbj.notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
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

    private List<Notification> notificationList = new ArrayList<Notification>();
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        adapter = new NotificationAdapter(MainActivity.this, R.layout.fruit_item, notificationList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "这是一个Toast提示", Toast.LENGTH_SHORT).show();

                Notification apple = new Notification("Apple", R.mipmap.ic_launcher);
                notificationList.add(apple);
                adapter.notifyDataSetChanged();
            }
        });

        MyThread thread = new MyThread(this);
        thread.start();
    }



    public void updateUI(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Notification item = new Notification(message, R.mipmap.ic_launcher);
                notificationList.add(item);
                adapter.notifyDataSetChanged();

                showNotification(message);

            }
        });
    }


    private int generateUniqueId() {
        Random random = new Random();
        return random.nextInt(1000000); // 使用随机数生成通知ID
    }

    private void showNotification(String message) {
        // 在这里添加弹出通知的代码
        int notificationId = generateUniqueId(); // 通知的唯一标识符
        String channelId = "my_channel_id"; // 通知渠道的ID，可以自定义

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.hello)
                .setContentTitle("New Message")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // 单击通知后自动取消通知

        // 创建一个意图，当用户点击通知时执行
//        Intent resultIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
//        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel"; // 通知渠道的名称
            String channelDescription = "My Channel Description"; // 通知渠道的描述
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