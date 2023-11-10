//package com.rabbitlbj.notificator;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{
//
//    List<Fruit> list;
//
//    public MyRecyclerViewAdapter(List<Fruit> list){
//        this.list = list;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.mItem = list.get(position);
//        final String itemId = list.get(position).id;
//        holder.mIdView.setText(list.get(position).id);
//        holder.mContentView.setText(list.get(position).content);
//        holder.mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //组件点击事件
//            }
//        });
//    }
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        public final View mView;
//        public final TextView mIdView;
//        public final TextView mContentView;
//        public DummyContent.DummyItem mItem;
//
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mIdView = (TextView) view.findViewById(R.id.id);
//            mContentView = (TextView) view.findViewById(R.id.content);
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
//    }
//}
//————————————————
//        版权声明：本文为CSDN博主「wf_kingofring」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/wf_kingofring/article/details/51384403
