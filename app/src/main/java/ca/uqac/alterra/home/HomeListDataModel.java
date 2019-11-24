package ca.uqac.alterra.home;

public class HomeListDataModel {

    private String mTitle;
    private String mImageRessource;
    private String mDistance;

    public HomeListDataModel(String txt, String image, String distance){
        mTitle = txt;
        mImageRessource = image;
        mDistance = distance;
    }

    public String getText(){
        return mTitle;
    }

    public String getImage(){
        return mImageRessource;
    }

    public String getDistance() {
        return mDistance;
    }
}
