package ca.uqac.alterra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<String> mPhotoList;

    public PicturesAdapter(Context mContext) {
        this.mContext = mContext;
        this.mPhotoList = new ArrayList<>();
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_pictures_layout,
                parent, false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       PlaceViewHolder Pholder =(PlaceViewHolder) holder;
       Glide.with(mContext).load(mPhotoList.get(position))
               .fitCenter().into(((PlaceViewHolder) holder).mPlace);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public void addPicture(String pictureUrl){
        mPhotoList.add(pictureUrl);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView mPlace;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.ivPlace);
        }
    }
}
