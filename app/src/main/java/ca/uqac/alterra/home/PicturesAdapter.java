package ca.uqac.alterra.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.ArrayList;

import ca.uqac.alterra.R;

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
       Glide.with(mContext)
            .load(mPhotoList.get(position))
            .fitCenter()
            .into(((PlaceViewHolder) holder).mPlace);

       Pholder.mPlace.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
                switchContext(mPhotoList.get(position));
           }
       });

       Pholder.mPlace.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               new MaterialAlertDialogBuilder(mContext)
                       .setTitle(R.string.profile_photos_dialog_box_title)
                       .setMessage(R.string.profile_photos_dialog_box_message)

                       // Specifying a listener allows you to take an action before dismissing the dialog.
                       // The dialog is automatically dismissed when a dialog button is clicked.
                       .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               // Continue with delete operation
                           }
                       })

                       // A null listener allows the button to dismiss the dialog and take no further action.
                       .setNegativeButton(android.R.string.no, null)
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .show();
               return true;
           }

       });
    }

    public void switchContext(String url){
        if(mContext instanceof HomeActivity){
            HomeActivity homeActivity =(HomeActivity) mContext;
            homeActivity.displayPicture(url);
        }
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
