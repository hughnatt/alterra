package ca.uqac.alterra.utility;

import java.text.DecimalFormat;

public class PrettyPrinter {

     public static String formatDistance(double distance){

        String distanceString;

        if(distance < 1000){
            distanceString = new DecimalFormat("#").format(distance) + " m";
        }
        else if(distance < 1000000){
            distanceString = new DecimalFormat("#.#").format(distance / 1000) + " km";
        }
        else {
            distanceString = "+999 km";
        }

        return distanceString;
    }
}
