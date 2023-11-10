package com.rabbitlbj.notificator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    private List<Fruit> fruitList = new ArrayList<Fruit>();
    private FruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFruits();
        adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "这是一个Toast提示", Toast.LENGTH_SHORT).show();

                Fruit apple = new Fruit("Apple", R.mipmap.ic_launcher);
                fruitList.add(apple);
                adapter.notifyDataSetChanged();
            }
        });

//        connectToServer("192.168.11.236", 8991);
        MyThread thread = new MyThread(this);
        thread.start();

    }

    private void initFruits() {
        for (int i = 0; i < 1; i++) {
            Fruit apple = new Fruit("Apple", R.mipmap.ic_launcher);
            fruitList.add(apple);
        }
    }

    public void updateUI(String message) {
        Log.d("TTTTT", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fruit apple = new Fruit(message, R.mipmap.ic_launcher);
                fruitList.add(apple);
                adapter.notifyDataSetChanged();

                showNotification(message);

            }
        });
    }

    public void connectToServer(String serverIp, int serverPort) {
        // 创建一个单线程池，用于执行网络操作
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 执行网络操作并获取结果
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                performNetworkOperation(serverIp, serverPort);
                return "";
            }
        });

        // 在UI线程中处理结果
        try {
            String result = future.get(); // 阻塞等待获取结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fruit apple = new Fruit(result, R.mipmap.ic_launcher);
                    fruitList.add(apple);
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 关闭线程池
        executorService.shutdown();
    }

    private void performNetworkOperation(String serverIp, int serverPort) {
        try {
            Socket socket = new Socket(serverIp, serverPort);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                StringBuilder stringBuilder = new StringBuilder();
                char[] buffer = new char[1024]; // 用于存储读取的字符
                int bytesRead;
                bytesRead = reader.read(buffer);
                stringBuilder.append(buffer, 0, bytesRead);
                String receivedData = stringBuilder.toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fruit apple = new Fruit(receivedData, R.mipmap.ic_launcher);
                        fruitList.add(apple);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int generateUniqueId() {
        Random random = new Random();
        return random.nextInt(1000000); // 使用随机数生成通知ID
    }

    private void showNotification(String message) {
        // 在这里添加弹出通知的代码
        int notificationId = generateUniqueId(); // 通知的唯一标识符
        String channelId = "my_channel_id"; // 通知渠道的ID，可以自定义

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.hello)
//                .setContentTitle("New Message")
//                .setContentText(message)
//                .setAutoCancel(true); // 单击通知后自动取消通知

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

        // 创建一个通知渠道（仅适用于 Android 8.0 及更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel"; // 通知渠道的名称
            String channelDescription = "My Channel Description"; // 通知渠道的描述
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 获取通知管理器
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // 发送通知
        notificationManager.notify(notificationId, builder.build());
    }
}