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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ca.uqac.alterra.R;

public class HomeListAdapter extends RecyclerView.Adapter {

    private ArrayList<HomeListDataModel> mDataList;
    public Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgView;
        public TextView titleView;
        public TextView distanceView;

        public MyViewHolder(View v) {
            super(v);
            imgView = v.findViewById(R.id.recyclerviewImage);
            titleView = v.findViewById(R.id.listRecyclerviewTitle);
            distanceView = v.findViewById(R.id.listRecyclerviewDistance);
        }

        public void setData(HomeListDataModel dm){
            this.titleView.setText(dm.getText());

            Glide.with(mContext)
                    .load(dm.getImage())
                    .centerCrop()
                    .into(this.imgView);

            this.distanceView.setText(dm.getDistance());
        }
    }

    public  HomeListAdapter(Context context){
        mDataList = new ArrayList<>();
        mContext = context;
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