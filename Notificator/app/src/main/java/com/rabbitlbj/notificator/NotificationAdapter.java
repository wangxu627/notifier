package com.rabbitlbj.notificator;

import static java.security.AccessController.getContext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public static class NotificationItem {
        public String title;
        public String content;

        public NotificationItem(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    public static NotificationItem createItem(String title, String content) {
        return new NotificationItem(title, content);
    }
    private List<NotificationItem> mData;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationItem> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationItem item = mData.get(position);
        holder.mTitle.setText(item.title);
        holder.mContent.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mContent;

        ViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.tvTitle);
            mContent = view.findViewById(R.id.tvContent);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    NotificationItem item = mData.get(pos);
                    copyToClipboard(item.title + "|" + item.content);
                    Toast.makeText(view.getContext(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
                    return true;// returning true instead of false, works for me
                }
            });
        }

        private void copyToClipboard(String text) {
            // 获取系统的剪贴板服务
            ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboardManager != null) {
                // 创建一个ClipData对象，并将文本放入剪贴板
                ClipData clipData = ClipData.newPlainText("label", text);
                clipboardManager.setPrimaryClip(clipData);
            }
        }

    }
}
