package ca.uqac.alterra.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraPicture;

public class PicturesAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<String> mPhotoList;
    private OnPictureClickListener mPictureClickListener;
    private OnPictureLongClickListener mPictureLongClickListener;

    public PicturesAdapter(Context mContext, @Nullable OnPictureClickListener pictureClickListener, @Nullable OnPictureLongClickListener pictureLongClickListener) {
        this.mContext = mContext;
        this.mPhotoList = new ArrayList<>();
        this.mPictureClickListener = pictureClickListener;
        this.mPictureLongClickListener = pictureLongClickListener;
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
       Glide.with(mContext)
            .load(mPhotoList.get(position))
            .fitCenter()
            .into(((PlaceViewHolder) holder).mPlace);

        Pholder.mPlace.setOnClickListener(view -> {
            if(mPictureClickListener != null){
                mPictureClickListener.onClick(mPhotoList.get(position));
            }
        });
        Pholder.mPlace.setOnLongClickListener(view -> {
            if(mPictureLongClickListener != null){
                mPictureLongClickListener.onLongClick(mPhotoList.get(position),position);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public void addPicture(String pictureUrl){
        mPhotoList.add(pictureUrl);
        notifyItemInserted(getItemCount());
    }

    public void addPicture(AlterraPicture alterraPicture){
        addPicture(alterraPicture.getURL());
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView mPlace;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.ivPlace);
        }
    }

    public void clear() {
        int size = mPhotoList.size();
        if (size > 0) {
            mPhotoList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public interface OnPictureClickListener{
        void onClick(String url);
    }

    public interface OnPictureLongClickListener{
        void onLongClick(String url,int position);
    }
}
