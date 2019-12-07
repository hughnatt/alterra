package ca.uqac.alterra.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import ca.uqac.alterra.R;
import ca.uqac.alterra.home.HomeActivity;
import ca.uqac.alterra.types.AlterraPoint;
import ca.uqac.alterra.utility.AlterraGeolocator;
import ca.uqac.alterra.utility.PrettyPrinter;

public class LocationAdapter extends RecyclerView.Adapter {

    private ArrayList<AlterraPoint> mDataList;
    private Context mContext;
    private OnButtonClickListener mOnButtonClickListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgView;
        private TextView titleView;
        private TextView distanceView;
        private Button button;
        private ImageButton photoButton;


        private MyViewHolder(View v) {
            super(v);
            imgView = v.findViewById(R.id.recyclerviewImage);
            titleView = v.findViewById(R.id.listRecyclerviewTitle);
            distanceView = v.findViewById(R.id.listRecyclerviewDistance);
            button = v.findViewById(R.id.listRecyclerviewButton);
            photoButton = v.findViewById(R.id.homeListPhotoButton);
        }

        private void setData(AlterraPoint alterraPoint){
            this.titleView.setText(alterraPoint.getTitle());

            photoButton.setOnClickListener(view -> {
                if(mOnButtonClickListener != null){
                    mOnButtonClickListener.onClick(alterraPoint);
                }
            });

            Glide.with(mContext)
                    .load(alterraPoint.getThumbnail())
                    .centerCrop()
                    .into(this.imgView);

            this.distanceView.setText(PrettyPrinter.formatDistance(AlterraGeolocator.distanceFrom(alterraPoint)));

            if(alterraPoint.isUnlocked()){
                button.setText(mContext.getString(R.string.alterra_point_unlocked));
                button.setAlpha(1);
                button.setClickable(true);
                button.setOnClickListener(view -> {
                    ((HomeActivity) mContext).showPlaceDetails(alterraPoint);
                });
            }
            else if(alterraPoint.isUnlockable()){
                button.setText(mContext.getString(R.string.alterra_point_unlockable));
                button.setAlpha(1);
                button.setClickable(true);
                button.setOnClickListener(view -> {
                    alterraPoint.unlock();
                    button.setAlpha(1);
                    notifyItemChanged(getAdapterPosition());
                });
            }
            else{
                button.setText(mContext.getString(R.string.alterra_point_locked));
                button.setClickable(false);
                button.setAlpha(.5f);
            }
        }

    }

    public LocationAdapter(Context context, @Nullable OnButtonClickListener onClickListener){
        mDataList = new ArrayList<>();
        mContext = context;
        mOnButtonClickListener = onClickListener;
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
        Collections.sort(mDataList, (pointA, pointB) -> Double.compare(AlterraGeolocator.distanceFrom(pointA), AlterraGeolocator.distanceFrom(pointB)));
        vh.setData(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addPoint(AlterraPoint alterraPoint){
        mDataList.add(alterraPoint);
        notifyItemInserted(getItemCount());
    }

    public void clear(){

        int size = mDataList.size();

        if(size > 0){
            mDataList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public interface OnButtonClickListener{
        void onClick(AlterraPoint point);
    }

}