package ca.uqac.alterra.adapters;


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
import ca.uqac.alterra.types.AlterraPicture;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PictureViewHolder>{

    private Context mContext;
    private int mViewResId = R.layout.recycleview_pictures_layout;
    private ArrayList<AlterraPicture> mPhotoList;
    private OnPictureClickListener mPictureClickListener;
    private OnPictureLongClickListener mPictureLongClickListener;

    /*
    Constructor used in the ProfileFragment & DetailsFragment
     */
    public PicturesAdapter(Context mContext, @Nullable OnPictureClickListener pictureClickListener, @Nullable OnPictureLongClickListener pictureLongClickListener) {
        this.mContext = mContext;
        this.mPhotoList = new ArrayList<>();
        this.mPictureClickListener = pictureClickListener;
        this.mPictureLongClickListener = pictureLongClickListener;
    }

    /*
    Constructor used in the bottom-sheet from HomeActivity
     */
    public PicturesAdapter(Context context, @Nullable OnPictureClickListener pictureClickListener, @Nullable OnPictureLongClickListener pictureLongClickListener, int viewResId){
        this(context,pictureClickListener,pictureLongClickListener);
        mViewResId = viewResId;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mViewResId,
                parent, false);

        return new PictureViewHolder(view);
    }

    /*
    Put the picture from "position" index into the Holder of the recuyclerView
    Set the listener on the image to handle both onClick & onLongClick
     */
    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        Glide.with(mContext)
            .load(mPhotoList.get(position).getURL())
            .fitCenter()
            .into(holder.getImageView());

        holder.getImageView().setOnClickListener(view -> {
            if(mPictureClickListener != null){
                mPictureClickListener.onClick(mPhotoList.get(position));
            }
        });
        holder.getImageView().setOnLongClickListener(view -> {
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

    /*
    Method used to add a picture into the ArrayList to allow the futur display of this picture
    notify that an item was inserted to triggered the autorefresh
     */
    public void addPicture(AlterraPicture alterraPicture){
        mPhotoList.add(alterraPicture);
        notifyItemInserted(getItemCount());
    }

    /*
    Method used each time a deletion from Firebase & Firestore were successful
    Delete the image at the "position" index & notify this action to remove it
    from the recycler view
     */
    public void deleteItem(int position) {
        mPhotoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPhotoList.size());
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        PictureViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.ivPlace);
        }

        ImageView getImageView(){
            return mImageView;
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
        void onClick(AlterraPicture alterraPicture);
    }

    public interface OnPictureLongClickListener{
        void onLongClick(AlterraPicture alterraPicture,int position);
    }
}
