package ca.uqac.alterra.home;

import java.text.DecimalFormat;

public class HomeListDataModel {

    private String mTitle;
    private String mImageRessource;
    private double mDistance;
    private AlterraPoint mPoint;

    public HomeListDataModel(AlterraPoint point, double distance){
        mPoint = point;
        mTitle = point.getTitle();
        mImageRessource = point.getThumbnail();
        mDistance = distance;
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
            distanceString = new DecimalFormat("#.#").format(mDistance / 1000) + " km";
        }
        else {
            distanceString = "+999 km";
        }

        return distanceString;
    }

    public boolean isUnlocked(){
        return mPoint.isUnlocked();
    }

    public boolean isUnlockable(){
        return (mDistance < HomeActivity.MINIMUM_UNLOCK_DISTANCE);
    }

    public AlterraPoint getAlterraPoint(){
        return mPoint;
    }
}
