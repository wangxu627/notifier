package com.rabbitlbj.notificator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    public NotificationAdapter(List<NotificationItem> data) {
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

        }
    }
}
