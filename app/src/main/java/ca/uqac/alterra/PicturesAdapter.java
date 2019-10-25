package ca.uqac.alterra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<Bitmap> mBitMap;

    public PicturesAdapter(Context mContext, ArrayList<String> PlaceList) {
        this.mContext = mContext;
        this.mBitMap = new ArrayList<>();
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
       Pholder.mPlace.setImageBitmap(mBitMap.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitMap.size();
    }

    public void addPicture(String pictureUrl){
        try {
            URL myUrl = new URL(pictureUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(myUrl.openConnection().getInputStream());
            System.out.println("bitmap "+bitmap);
            mBitMap.add(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView mPlace;

        public PlaceViewHolder(View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.ivPlace);
        }
    }
}
