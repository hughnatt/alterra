package ca.uqac.alterra.home;

public class HomeListDataModel {

    private String text;
    private String imageRessource;

    public HomeListDataModel(String txt, String image){
        this.text = txt;
        this.imageRessource = image;
    }

    public String getText(){
        return this.text;
    }

    public String getImage(){
        return this.imageRessource;
    }

}
