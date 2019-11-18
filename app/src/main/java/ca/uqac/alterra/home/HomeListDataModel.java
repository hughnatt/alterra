package ca.uqac.alterra.home;

public class HomeListDataModel {

    private String text;
    private int imageRessource;

    public HomeListDataModel(String txt, int image){
        this.text = txt;
        this.imageRessource = image;
    }

    public String getText(){
        return this.text;
    }

    public int getImage(){
        return this.imageRessource;
    }

}
