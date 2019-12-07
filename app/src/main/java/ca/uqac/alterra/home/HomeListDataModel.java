package ca.uqac.alterra.home;

import ca.uqac.alterra.types.AlterraPoint;

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

    public double getDistance() {
        return mDistance;
    }

    public boolean isUnlocked(){
        return mPoint.isUnlocked();
    }

    public boolean isUnlockable(){
        return mPoint.isUnlockable();
    }

    public AlterraPoint getAlterraPoint(){
        return mPoint;
    }
}
