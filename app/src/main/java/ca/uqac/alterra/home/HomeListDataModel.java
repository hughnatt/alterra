package ca.uqac.alterra.home;

import java.text.DecimalFormat;

public class HomeListDataModel {

    private String mTitle;
    private String mImageRessource;
    private double mDistance;
    private boolean mUnlocked;

    public HomeListDataModel(String txt, String image, double distance, Boolean unlocked){
        mTitle = txt;
        mImageRessource = image;
        mDistance = distance;
        mUnlocked = unlocked;
    }

    public String getText(){
        return mTitle;
    }

    public String getImage(){
        return mImageRessource;
    }

    public String getDistance() {

        String distanceString;

        if(mDistance < 1000){
            distanceString = new DecimalFormat("#.##").format(mDistance) + " m";
        }
        else if(mDistance < 1000000){
            mDistance /= 1000;
            distanceString = new DecimalFormat("#.#").format(mDistance) + " km";
        }
        else {
            distanceString = "+999 km";
        }

        return distanceString;
    }

    public Boolean isUnlocked(){
        return mUnlocked;
    }

    public  Boolean isUnlockable(){
        return (mDistance < HomeActivity.MINIMUM_UNLOCK_DISTANCE);
    }
}
