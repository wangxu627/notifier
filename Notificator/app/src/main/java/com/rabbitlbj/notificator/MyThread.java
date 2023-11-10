package com.rabbitlbj.notificator;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Function;

class MyThread extends Thread {
    private MainActivity activity;
    public MyThread(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        // 线程执行的代码
        performNetworkOperation("192.168.11.236", 8991);
    }

    private void performNetworkOperation(String serverIp, int serverPort) {
        try {
            Socket socket = new Socket(serverIp, serverPort);
            // 获取输入流
            InputStream inputStream = socket.getInputStream();
            // 读取服务器发送的消息
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                // 处理接收到的消息，例如更新界面等操作...
                Log.e("TTTTT", message);
                this.activity.updateUI(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
