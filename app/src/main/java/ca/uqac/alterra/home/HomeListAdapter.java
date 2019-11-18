package ca.uqac.alterra.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.uqac.alterra.R;

public class HomeListAdapter extends RecyclerView.Adapter {

    private ArrayList<HomeListDataModel> mDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgView;
        public TextView textView;
        public LinearLayout linearLayout;

        public MyViewHolder(View v) {
            super(v);
            imgView = v.findViewById(R.id.recyclerviewImage);
            textView = v.findViewById(R.id.recyclerviewText);
            linearLayout = v.findViewById(R.id.recyclerviewLinear);
        }

        public void setData(HomeListDataModel dm){
            this.textView.setText(dm.getText());
            this.imgView.setImageResource(dm.getImage());
        }
    }

    public  HomeListAdapter(){
        mDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_home_list_layout,
                parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addData(HomeListDataModel data){
        mDataList.add(data);
    }
}